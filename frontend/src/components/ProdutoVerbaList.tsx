import React, { useEffect, useState } from 'react';
import { EmpresaFiltro, ProdutoVerba } from '../types/acordo';
import { listarEmpresas, listarProdutosVerba } from '../services/api';

export const ProdutoVerbaList: React.FC = () => {
  const [produtos, setProdutos] = useState<ProdutoVerba[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [empresas, setEmpresas] = useState<EmpresaFiltro[]>([]);

  // Filtros pendentes (selects)
  const [selEmpresa, setSelEmpresa] = useState(() => localStorage.getItem('filtroProdutoEmpresa') || '');
  const [selVigencia, setSelVigencia] = useState(() => localStorage.getItem('filtroProdutoVigencia') || 'vigente');
  const [selStatus, setSelStatus] = useState(() => localStorage.getItem('filtroProdutoVerba') || '');
  const [selOrdenacao, setSelOrdenacao] = useState(() => localStorage.getItem('ordenacaoProduto') || 'codigo');

  // Filtros aplicados
  const [filtroEmpresa, setFiltroEmpresa] = useState(selEmpresa);
  const [filtroVigencia, setFiltroVigencia] = useState(selVigencia);
  const [filtroStatus, setFiltroStatus] = useState(selStatus);
  const [ordenacao, setOrdenacao] = useState(selOrdenacao);

  const temAlteracoes = selEmpresa !== filtroEmpresa || selVigencia !== filtroVigencia
    || selStatus !== filtroStatus || selOrdenacao !== ordenacao;

  const aplicarFiltros = () => {
    localStorage.setItem('filtroProdutoEmpresa', selEmpresa);
    localStorage.setItem('filtroProdutoVigencia', selVigencia);
    localStorage.setItem('filtroProdutoVerba', selStatus);
    localStorage.setItem('ordenacaoProduto', selOrdenacao);
    setFiltroEmpresa(selEmpresa);
    setFiltroVigencia(selVigencia);
    setFiltroStatus(selStatus);
    setOrdenacao(selOrdenacao);
  };

  const produtosFiltrados = [...produtos]
    .filter(p => filtroEmpresa === '' || p.cdEmpresa === Number(filtroEmpresa))
    .filter(p => filtroVigencia === '' ? true : filtroVigencia === 'vigente' ? p.vigente : !p.vigente)
    .filter(p => filtroStatus === '' ? true : filtroStatus === 'ok' ? p.correto : !p.correto)
    .sort((a, b) => {
      if (ordenacao === 'descricao') {
        return (a.txProdutoDescricao || '').localeCompare(b.txProdutoDescricao || '');
      }
      return a.cdProduto - b.cdProduto;
    });

  useEffect(() => {
    listarEmpresas().then(setEmpresas).catch(() => {});
    listarProdutosVerba()
      .then(setProdutos)
      .catch((e) => setErro('Erro ao carregar produtos: ' + e.message))
      .finally(() => setLoading(false));
    const interval = setInterval(() => {
      listarProdutosVerba().then(setProdutos).catch(() => {});
    }, 30000);
    return () => clearInterval(interval);
  }, []);

  if (loading) return <div className="loading">Carregando produtos...</div>;
  if (erro) return <div className="error-message">{erro}</div>;

  const qtCorretos = produtosFiltrados.filter(p => p.correto).length;
  const qtInconsistentes = produtosFiltrados.filter(p => !p.correto).length;

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Produtos com Verba</h1>
        <select className="filtro-tipo" value={selEmpresa} onChange={(e) => setSelEmpresa(e.target.value)}>
          <option value="">Todas empresas</option>
          {empresas.map(e => (
            <option key={e.cod} value={e.cod}>{e.cod} - {e.nome}</option>
          ))}
        </select>
        <select className="filtro-tipo" value={selVigencia} onChange={(e) => setSelVigencia(e.target.value)}>
          <option value="">Todas vigências</option>
          <option value="vigente">Vigentes</option>
          <option value="nao-vigente">Não vigentes</option>
        </select>
        <select className="filtro-tipo" value={selStatus} onChange={(e) => setSelStatus(e.target.value)}>
          <option value="">Todos</option>
          <option value="ok">Corretos</option>
          <option value="erro">Inconsistentes</option>
        </select>
        <select className="filtro-tipo" value={selOrdenacao} onChange={(e) => setSelOrdenacao(e.target.value)}>
          <option value="codigo">Ordenar por Código</option>
          <option value="descricao">Ordenar por Descrição</option>
        </select>
        <button
          className="btn-aplicar"
          onClick={aplicarFiltros}
          disabled={!temAlteracoes}
        >
          Aplicar
        </button>
        <span className="record-count">
          {produtosFiltrados.length} registro(s)
          {filtroStatus === '' && <> &mdash; <span style={{ color: 'var(--color-green)' }}>{qtCorretos} OK</span> / <span style={{ color: 'var(--color-red)' }}>{qtInconsistentes} inconsistentes</span></>}
        </span>
      </div>

      <div className="table-scroll">
        <table className="data-table">
          <thead>
            <tr>
              <th style={{ minWidth: 220 }}>Empresa</th>
              <th style={{ minWidth: 220 }}>Produto</th>
              <th>Sell-Out</th>
              <th>Sell-In</th>
              <th>Antecip.</th>
              <th>Cta Com.</th>
              <th>Ac. Vig.</th>
              <th>Tipo</th>
              <th>Vl Acordo</th>
              <th>% Acordo</th>
              <th>Vl Esperado</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {produtosFiltrados.map((p, i) => (
              <tr key={`${p.cdEmpresa}-${p.cdProduto}-${i}`}>
                <td>{p.cdEmpresa} - {empresas.find(e => e.cod === p.cdEmpresa)?.nome || ''}</td>
                <td>{p.cdProduto} - {p.txProdutoDescricao || ''}</td>
                <td className="cell-number">{formatDecimal(p.vlVerbaSellOut)}</td>
                <td className="cell-number">{formatDecimal(p.vlVerbaSellIn)}</td>
                <td className="cell-number">{formatDecimal(p.vlVerbaAntecipacao)}</td>
                <td className="cell-number">{formatDecimal(p.vlVerbaContaComercial)}</td>
                <td className="cell-number">{p.nrAcordoVigente || '-'}</td>
                <td>{`${(p.txTipoAcordo || '').trim()}${(p.txSubtipoAcordo || '').trim()}`}</td>
                <td className="cell-number">{formatDecimal(p.vlUnidadeAcordo)}</td>
                <td className="cell-number">{formatDecimal(p.pcUnidadeAcordo)}</td>
                <td className="cell-number">{formatDecimal(p.vlVerbaEsperada)}</td>
                <td style={{ textAlign: 'center' }}>
                  {p.correto ? (
                    <span className="status-badge" style={{ background: 'var(--color-green-bg)', color: 'var(--color-green)' }}>OK</span>
                  ) : p.motivoInconsistencia?.startsWith('Preço de venda zerado') ? (
                    <span className="status-badge" style={{ background: '#3d3200', color: '#f0c000' }}>Preço zerado</span>
                  ) : (
                    <span className="status-badge" style={{ background: 'var(--color-red-bg)', color: 'var(--color-red)' }}>Inconsistente</span>
                  )}
                </td>
              </tr>
            ))}
            {produtosFiltrados.length === 0 && (
              <tr>
                <td colSpan={12} className="empty-cell">Nenhum produto com verba encontrado</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

function formatDecimal(value: number | null): string {
  if (value === null || value === undefined || value === 0) return '-';
  return value.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 4 });
}
