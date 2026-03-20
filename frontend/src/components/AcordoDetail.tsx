import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { AcordoDetalhado, VendaDTO, VendaPeriodoDTO } from '../types/acordo';
import { buscarAcordoDetalhado, buscarVendas, buscarVendasPorPeriodo, atualizarPeriodo, atualizarAcordoPeriodo } from '../services/api';
import { StatusBadge } from './StatusBadge';
import { ProcessingPipeline } from './ProcessingPipeline';

export const AcordoDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [acordo, setAcordo] = useState<AcordoDetalhado | null>(null);
  const [vendas, setVendas] = useState<VendaDTO[]>([]);
  const [vendasPeriodo, setVendasPeriodo] = useState<VendaPeriodoDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadingVendas, setLoadingVendas] = useState(false);
  const [loadingVendasPeriodo, setLoadingVendasPeriodo] = useState(false);
  const [editingPeriodo, setEditingPeriodo] = useState<Record<number, { dtInicio: string; dtFim: string }>>({});
  const [salvandoPeriodo, setSalvandoPeriodo] = useState<number | null>(null);
  const [acordoInicio, setAcordoInicio] = useState('');
  const [acordoFim, setAcordoFim] = useState('');
  const [salvandoAcordo, setSalvandoAcordo] = useState(false);
  const [erro, setErro] = useState<string | null>(null);
  const [collapsed, setCollapsed] = useState<Record<string, boolean>>({});

  const numId = Number(id);

  function recarregar(showLoading = false) {
    if (showLoading) setLoading(true);
    buscarAcordoDetalhado(numId)
      .then((data) => {
        setAcordo(data);
        setAcordoInicio(toInputDate(data.dtAcordoInicio));
        setAcordoFim(toInputDate(data.dtAcordoFim));
        buscarVendasPorPeriodo(numId).then(setVendasPeriodo).catch(() => {});
      })
      .catch((e) => { if (showLoading) setErro('Erro ao carregar acordo: ' + e.message); })
      .finally(() => { if (showLoading) setLoading(false); });
  }

  useEffect(() => {
    if (!id) return;
    recarregar(true);
    const interval = setInterval(() => recarregar(), 10000);
    return () => clearInterval(interval);
  }, [id]);

  const toggleSection = (key: string) => {
    setCollapsed((prev) => ({ ...prev, [key]: !prev[key] }));
  };

  if (loading) {
    return <div className="loading">Carregando detalhes do acordo...</div>;
  }

  if (erro || !acordo) {
    return (
      <div className="page-container">
        <div className="error-message">{erro || 'Acordo nao encontrado'}</div>
        <button className="btn-back" onClick={() => navigate('/')}>Voltar</button>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="detail-header">
        <button className="btn-back" onClick={() => navigate('/')}>
          ← Voltar
        </button>
        <h1>
          Acordo #{acordo.nrAcordo}
          <StatusBadge codigo={acordo.txSituacao} descricao={acordo.situacaoDescricao} />
        </h1>
      </div>

      {/* Cabecalho */}
      <Section title="Cabecalho" sectionKey="cabecalho" collapsed={collapsed} toggle={toggleSection}>
        <div className="info-grid">
          <InfoField label="ID Acordo" value={acordo.idAcordo} />
          <InfoField label="Nr. Acordo" value={acordo.nrAcordo} />
          <InfoField label="Situacao" value={acordo.situacaoDescricao} />
          <InfoField label="Tipo" value={acordo.txTipo} />
          <InfoField label="Subtipo" value={acordo.txSubtipo} />
          <InfoField label="Empresa" value={acordo.cdEmpresa} />
          <InfoField label="Usuario" value={acordo.cdUsuario} />
          <div className="info-field">
            <span className="info-label">Periodo Inicio</span>
            <input type="date" className="input-edit" value={acordoInicio} onChange={e => setAcordoInicio(e.target.value)} />
          </div>
          <div className="info-field">
            <span className="info-label">Periodo Fim</span>
            <input type="date" className="input-edit" value={acordoFim} onChange={e => setAcordoFim(e.target.value)} />
          </div>
          <div className="info-field" style={{ justifyContent: 'flex-end' }}>
            <button
              className="btn-save"
              disabled={salvandoAcordo}
              onClick={async () => {
                setSalvandoAcordo(true);
                try {
                  await atualizarAcordoPeriodo(numId, acordoInicio, acordoFim);
                  const data = await buscarAcordoDetalhado(numId);
                  setAcordo(data);
                  setAcordoInicio(toInputDate(data.dtAcordoInicio));
                  setAcordoFim(toInputDate(data.dtAcordoFim));
                  buscarVendas(numId).then(setVendas).catch(() => {});
                  if (data.txTipo === 'S' && data.txSubtipo === 'O') {
                    buscarVendasPorPeriodo(numId).then(setVendasPeriodo).catch(() => {});
                  }
                } catch {
                  alert('Erro ao salvar periodo do acordo');
                } finally {
                  setSalvandoAcordo(false);
                }
              }}
            >
              {salvandoAcordo ? '...' : 'Salvar Periodo'}
            </button>
          </div>
          <InfoField label="Cadastro" value={formatDateTime(acordo.dtCadastro)} />
          <InfoField label="Manutencao" value={formatDateTime(acordo.dtManutencao)} />
          {acordo.transacionador && (
            <>
              <InfoField label="Fornecedor" value={acordo.transacionador.cdFornecedor} />
              <InfoField label="Comprador" value={acordo.transacionador.cdComprador} />
            </>
          )}
        </div>
      </Section>

      {/* Configuracao Sell Out */}
      {acordo.sellOutTotal && (
        <Section title="Configuracao Sell Out" sectionKey="config" collapsed={collapsed} toggle={toggleSection}>
          <div className="info-grid">
            <InfoField
              label="Fechamento Manual"
              value={acordo.sellOutTotal.flFechamentoManual === 'S' ? 'Sim' : 'Nao'}
            />
            <InfoField label="Forma Pagamento" value={acordo.sellOutTotal.cdFormaPagamento} />
            <InfoField label="Condicao Pagamento" value={acordo.sellOutTotal.cdCondicaoPagamento} />
            <InfoField label="Banco" value={acordo.sellOutTotal.cdBanco} />
            <InfoField label="Controle" value={acordo.sellOutTotal.txControle} />
            <InfoField
              label="Atribuir Verba Conta Comercial"
              value={acordo.sellOutTotal.flAtribuirVerbaContaComercial === 'S' ? 'Sim' : 'Nao'}
            />
            {acordo.sellOutTotal.txMensagem && (
              <InfoField label="Mensagem" value={acordo.sellOutTotal.txMensagem} wide />
            )}
          </div>
        </Section>
      )}

      {/* Empresas */}
      <Section
        title={`Empresas (${acordo.empresas.length})`}
        sectionKey="empresas"
        collapsed={collapsed}
        toggle={toggleSection}
      >
        {acordo.empresas.length > 0 ? (
          <div className="tag-list">
            {acordo.empresas.map((emp, idx) => (
              <span key={idx} className="tag">
                Empresa {emp.cdEmpresa}
              </span>
            ))}
          </div>
        ) : (
          <p className="empty-text">Nenhuma empresa cadastrada</p>
        )}
      </Section>

      {/* Distribuicao */}
      <Section
        title={`Distribuicao (${acordo.distribuicoes.length})`}
        sectionKey="distribuicao"
        collapsed={collapsed}
        toggle={toggleSection}
      >
        {acordo.distribuicoes.length > 0 ? (
          <table className="data-table">
            <thead>
              <tr>
                <th>Empresa</th>
                <th>Origem</th>
                <th>Vl. Distribuido</th>
                <th>% Distribuido</th>
              </tr>
            </thead>
            <tbody>
              {acordo.distribuicoes.map((d, idx) => (
                <tr key={idx}>
                  <td className="cell-number">{d.cdEmpresa}</td>
                  <td className="cell-number">{d.cdOrigem}</td>
                  <td className="cell-number">{formatCurrency(d.vlDistribuido)}</td>
                  <td className="cell-number">{formatPercent(d.pcDistribuido)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p className="empty-text">Sem distribuicao</p>
        )}
      </Section>

      {/* Produtos / Vendas por Periodo (Sell Out) */}
      {acordo.txTipo === 'S' && acordo.txSubtipo === 'O' && (
        <Section
          title={`Produtos / Vendas por Periodo (${loadingVendasPeriodo ? '...' : vendasPeriodo.length})`}
          sectionKey="vendasPeriodo"
          collapsed={collapsed}
          toggle={toggleSection}
        >
          {loadingVendasPeriodo ? (
            <div className="loading">Carregando vendas por periodo...</div>
          ) : vendasPeriodo.length > 0 ? (
            <div className="table-scroll">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Empresa</th>
                    <th>Produto</th>
                    <th>Descricao</th>
                    <th>% Unidade</th>
                    <th>Vl. Unidade</th>
                    <th>Dt. Inicio</th>
                    <th>Dt. Fim</th>
                    <th>Qtd. Vendida</th>
                    <th>Vl. Total</th>
                    <th>Notas</th>
                  </tr>
                </thead>
                <tbody>
                  {vendasPeriodo.map((vp, idx) => (
                    <tr key={idx}>
                      <td className="cell-number">{vp.cdEmpresa}</td>
                      <td className="cell-number">{vp.cdProduto}</td>
                      <td>{vp.txProdutoDescricao || '-'}</td>
                      <td className="cell-number">{formatPercent(vp.pcUnidade)}</td>
                      <td className="cell-number">{formatCurrency(vp.vlUnidade)}</td>
                      <td>{formatDate(vp.dtInicio)}</td>
                      <td>{formatDate(vp.dtFim)}</td>
                      <td className="cell-number">{formatNumber(vp.quantidadeVendida, 4)}</td>
                      <td className="cell-number">{formatCurrency(vp.valorTotal)}</td>
                      <td className="cell-number">{vp.quantidadeNotas}</td>
                    </tr>
                  ))}
                </tbody>
                <tfoot>
                  <tr style={{ fontWeight: 600 }}>
                    <td colSpan={7} style={{ textAlign: 'right', padding: '8px 12px' }}>Total</td>
                    <td className="cell-number">
                      {formatNumber(vendasPeriodo.reduce((s, v) => s + (v.quantidadeVendida || 0), 0), 4)}
                    </td>
                    <td className="cell-number">
                      {formatCurrency(vendasPeriodo.reduce((s, v) => s + (v.valorTotal || 0), 0))}
                    </td>
                    <td className="cell-number">
                      {vendasPeriodo.reduce((s, v) => s + v.quantidadeNotas, 0)}
                    </td>
                  </tr>
                </tfoot>
              </table>
            </div>
          ) : (
            <p className="empty-text">Nenhum produto/venda encontrado nos periodos</p>
          )}
        </Section>
      )}

      {/* Produtos (quando não é Sell Out) */}
      {!(acordo.txTipo === 'S' && acordo.txSubtipo === 'O') && acordo.itens.length > 0 && (
        <Section
          title={`Produtos (${acordo.itens.length})`}
          sectionKey="produtos"
          collapsed={collapsed}
          toggle={toggleSection}
        >
          <table className="data-table">
            <thead>
              <tr>
                <th>Codigo</th>
                <th>Produto</th>
                <th>% Unidade</th>
                <th>Vl. Unidade</th>
              </tr>
            </thead>
            <tbody>
              {acordo.itens.map((item, idx) => (
                <tr key={idx}>
                  <td className="cell-number">{item.cdProduto}</td>
                  <td>{item.txProdutoDescricao || '-'}</td>
                  <td className="cell-number">{formatPercent(item.pcUnidade)}</td>
                  <td className="cell-number">{formatCurrency(item.vlUnidade)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </Section>
      )}

      {/* Processamento */}
      <Section
        title="Processamento"
        sectionKey="pipeline"
        collapsed={collapsed}
        toggle={toggleSection}
      >
        {acordo.processamentos.length > 0 || acordo.processamentoItens.length > 0 ? (
          <ProcessingPipeline
            processamentos={acordo.processamentos}
            processamentoItens={acordo.processamentoItens}
          />
        ) : (
          <p className="empty-text">Sem dados de processamento</p>
        )}
      </Section>

      {/* Periodos / Fragmentos */}
      <Section
        title={`Periodos / Fragmentos (${acordo.periodos.length})`}
        sectionKey="periodos"
        collapsed={collapsed}
        toggle={toggleSection}
      >
        {acordo.periodos.length > 0 ? (
          <table className="data-table">
            <thead>
              <tr>
                <th>Empresa</th>
                <th>Produto</th>
                <th>Dt. Inicio</th>
                <th>Dt. Fim</th>
              </tr>
            </thead>
            <tbody>
              {acordo.periodos.map((p, idx) => (
                <tr key={idx}>
                  <td className="cell-number">{p.cdEmpresa}</td>
                  <td className="cell-number">{p.cdProduto}</td>
                  <td>{formatDate(p.dtInicio)}</td>
                  <td>{formatDate(p.dtFim)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p className="empty-text">Nenhum periodo gerado</p>
        )}
      </Section>

      {/* Lotes */}
      <Section
        title={`Lotes (${acordo.lotes.length})`}
        sectionKey="lotes"
        collapsed={collapsed}
        toggle={toggleSection}
      >
        {acordo.lotes.length > 0 ? (
          <table className="data-table">
            <thead>
              <tr>
                <th>Lote</th>
                <th>Empresa</th>
                <th>Dt. Inicial</th>
                <th>Dt. Final</th>
                <th>Vl. Recebido</th>
                <th>Cadastro</th>
              </tr>
            </thead>
            <tbody>
              {acordo.lotes.map((l, idx) => (
                <tr key={idx}>
                  <td className="cell-number">{l.cdLote}</td>
                  <td className="cell-number">{l.cdEmpresa}</td>
                  <td>{formatDate(l.dtInicial)}</td>
                  <td>{formatDate(l.dtFinal)}</td>
                  <td className="cell-number">{formatCurrency(l.vlRecebido)}</td>
                  <td>{formatDateTime(l.dtCadastro)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p className="empty-text">Nenhum lote gerado</p>
        )}
      </Section>

      {/* Prestacoes */}
      <Section
        title={`Prestacoes (${acordo.prestacoes.length})`}
        sectionKey="prestacoes"
        collapsed={collapsed}
        toggle={toggleSection}
      >
        {acordo.prestacoes.length > 0 ? (
          <div className="table-scroll">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Documento</th>
                  <th>Prestacao</th>
                  <th>Emp. Acordo</th>
                  <th>Emp. Documento</th>
                  <th>Vl. Prestacao</th>
                  <th>Vl. Total Doc.</th>
                  <th>Vencimento</th>
                  <th>Utilizado</th>
                  <th>Nota</th>
                  <th>Serie</th>
                  <th>Origem</th>
                </tr>
              </thead>
              <tbody>
                {acordo.prestacoes.map((p, idx) => (
                  <tr key={idx}>
                    <td className="cell-number">{p.nrDocumento}</td>
                    <td className="cell-number">{p.nrPrestacao}</td>
                    <td className="cell-number">{p.cdEmpresaAcordo}</td>
                    <td className="cell-number">{p.cdEmpresaDocumento}</td>
                    <td className="cell-number">{formatCurrency(p.vlPrestacao)}</td>
                    <td className="cell-number">{formatCurrency(p.vlTotalDocumento)}</td>
                    <td>{formatDate(p.dtVencimento)}</td>
                    <td>
                      <span className={`flag ${p.flUtilizado === 'S' ? 'flag-sim' : 'flag-nao'}`}>
                        {p.flUtilizado === 'S' ? 'Sim' : 'Nao'}
                      </span>
                    </td>
                    <td className="cell-number">{p.nrNota || '-'}</td>
                    <td>{p.txSerieNota || '-'}</td>
                    <td className="cell-number">{p.cdOrigem}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <p className="empty-text">Nenhuma prestacao gerada</p>
        )}
      </Section>

      {/* Integracao */}
      <Section
        title={`Integracao Documento (${acordo.integracaoDocumentos.length})`}
        sectionKey="integracao"
        collapsed={collapsed}
        toggle={toggleSection}
      >
        {acordo.integracaoDocumentos.length > 0 ? (
          <table className="data-table">
            <thead>
              <tr>
                <th>Empresa</th>
                <th>Tipo</th>
                <th>Serie</th>
                <th>Fornecedor</th>
                <th>Numero</th>
              </tr>
            </thead>
            <tbody>
              {acordo.integracaoDocumentos.map((doc, idx) => (
                <tr key={idx}>
                  <td className="cell-number">{doc.cdEmpresa}</td>
                  <td>{doc.txTipo || '-'}</td>
                  <td>{doc.txSerie || '-'}</td>
                  <td className="cell-number">{doc.cdFornecedor}</td>
                  <td className="cell-number">{doc.nrNumero}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p className="empty-text">Sem documentos de integracao</p>
        )}
      </Section>

    </div>
  );
};

// --- Section component ---

interface SectionProps {
  title: string;
  sectionKey: string;
  collapsed: Record<string, boolean>;
  toggle: (key: string) => void;
  children: React.ReactNode;
}

const Section: React.FC<SectionProps> = ({ title, sectionKey, collapsed, toggle, children }) => {
  const isCollapsed = collapsed[sectionKey] || false;
  return (
    <div className="section-card">
      <div className="section-header" onClick={() => toggle(sectionKey)}>
        <h2>{title}</h2>
        <span className="section-toggle">{isCollapsed ? '+' : '-'}</span>
      </div>
      {!isCollapsed && <div className="section-body">{children}</div>}
    </div>
  );
};

// --- Info field ---

interface InfoFieldProps {
  label: string;
  value: string | number | null | undefined;
  wide?: boolean;
}

const InfoField: React.FC<InfoFieldProps> = ({ label, value, wide }) => (
  <div className={`info-field ${wide ? 'wide' : ''}`}>
    <span className="info-label">{label}</span>
    <span className="info-value">{value ?? '-'}</span>
  </div>
);

// --- Formatters ---

function formatDate(dt: string | null): string {
  if (!dt) return '-';
  const parts = dt.substring(0, 10).split('-');
  if (parts.length === 3) return `${parts[2]}/${parts[1]}/${parts[0]}`;
  return dt;
}

function formatDateTime(dt: string | null): string {
  if (!dt) return '-';
  try {
    const d = new Date(dt);
    const day = String(d.getDate()).padStart(2, '0');
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const year = d.getFullYear();
    const hours = String(d.getHours()).padStart(2, '0');
    const mins = String(d.getMinutes()).padStart(2, '0');
    return `${day}/${month}/${year} ${hours}:${mins}`;
  } catch {
    return dt;
  }
}

function formatCurrency(value: number | null | undefined): string {
  if (value === null || value === undefined) return '-';
  return `R$ ${Number(value).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 4 })}`;
}

function formatPercent(value: number | null | undefined): string {
  if (value === null || value === undefined) return '-';
  return `${Number(value).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 4 })}%`;
}

function toInputDate(dt: string | null): string {
  if (!dt) return '';
  return dt.substring(0, 10);
}

function formatNumber(value: number | null | undefined, decimals: number = 2): string {
  if (value === null || value === undefined) return '-';
  return Number(value).toLocaleString('pt-BR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: decimals,
  });
}
