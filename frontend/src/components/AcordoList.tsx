import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AcordoComercial } from '../types/acordo';
import { listarAcordos, limparTudo } from '../services/api';
import { StatusBadge } from './StatusBadge';

const TIPOS_ACORDO = [
  { valor: '', label: 'Todos' },
  { valor: 'SO', label: 'Sell-Out' },
  { valor: 'SI', label: 'Sell-In' },
  { valor: 'BP', label: 'Bonificação de Produto' },
  { valor: 'AV', label: 'Antecipação de Verba' },
];

export const AcordoList: React.FC = () => {
  const [acordos, setAcordos] = useState<AcordoComercial[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [confirmacao, setConfirmacao] = useState<{ msg: string; onOk: () => void } | null>(null);
  const [mensagem, setMensagem] = useState('');
  const [mensagemTipo, setMensagemTipo] = useState<'sucesso' | 'erro' | ''>('');
  const navigate = useNavigate();

  // Filtros pendentes (selects)
  const [selTipo, setSelTipo] = useState(() => localStorage.getItem('filtroTipoAcordo') || '');
  const [selVigencia, setSelVigencia] = useState(() => localStorage.getItem('filtroVigenciaAcordo') || '');

  // Filtros aplicados
  const [filtroTipo, setFiltroTipo] = useState(selTipo);
  const [filtroVigencia, setFiltroVigencia] = useState(selVigencia);

  const temAlteracoes = selTipo !== filtroTipo || selVigencia !== filtroVigencia;

  const aplicarFiltros = () => {
    localStorage.setItem('filtroTipoAcordo', selTipo);
    localStorage.setItem('filtroVigenciaAcordo', selVigencia);
    setFiltroTipo(selTipo);
    setFiltroVigencia(selVigencia);
  };

  const acordosFiltrados = acordos
    .filter((a) => filtroTipo === '' || a.txTipo + a.txSubtipo === filtroTipo)
    .filter((a) => filtroVigencia === '' ? true : filtroVigencia === 'vigente' ? isVigente(a) : !isVigente(a));

  useEffect(() => {
    listarAcordos()
      .then(setAcordos)
      .catch((e) => setErro('Erro ao carregar acordos: ' + e.message))
      .finally(() => setLoading(false));
    const interval = setInterval(() => {
      listarAcordos().then(setAcordos).catch(() => {});
    }, 10000);
    return () => clearInterval(interval);
  }, []);

  if (loading) {
    return <div className="loading">Carregando acordos...</div>;
  }

  if (erro) {
    return <div className="error-message">{erro}</div>;
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Acordos Comerciais</h1>
        <select className="filtro-tipo" value={selTipo} onChange={(e) => setSelTipo(e.target.value)}>
          {TIPOS_ACORDO.map((t) => (
            <option key={t.valor} value={t.valor}>{t.label}</option>
          ))}
        </select>
        <select className="filtro-tipo" value={selVigencia} onChange={(e) => setSelVigencia(e.target.value)}>
          <option value="">Todas vigências</option>
          <option value="vigente">Vigentes</option>
          <option value="nao-vigente">Não vigentes</option>
        </select>
        <button className="btn-aplicar" onClick={aplicarFiltros} disabled={!temAlteracoes}>
          Aplicar
        </button>
        <span className="record-count">{acordosFiltrados.length} registro(s)</span>
        <button
          className="btn-limpar"
          onClick={() => setConfirmacao({
            msg: 'Tem certeza que deseja excluir TODOS os acordos e vendas simuladas? Esta ação não pode ser desfeita.',
            onOk: async () => {
              setConfirmacao(null);
              try {
                const res = await limparTudo();
                setMensagem(res.mensagem);
                setMensagemTipo('sucesso');
                setAcordos([]);
              } catch (e: any) {
                setMensagem('Erro ao limpar: ' + e.message);
                setMensagemTipo('erro');
              }
            }
          })}
        >
          Limpar tudo
        </button>
      </div>

      {mensagem && (
        <div className={mensagemTipo === 'sucesso' ? 'msg-sucesso' : 'error-message'}>{mensagem}</div>
      )}

      <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nr. Acordo</th>
            <th>Tipo</th>
            <th>Situacao</th>
            <th>Vigente</th>
            <th>Empresa</th>
            <th>Periodo Inicio</th>
            <th>Periodo Fim</th>
            <th>Cadastro</th>
            <th>Manutencao</th>
          </tr>
        </thead>
        <tbody>
          {acordosFiltrados.map((acordo) => (
            <tr
              key={acordo.idAcordo}
              className="clickable-row"
              onClick={() => navigate(`/acordos/${acordo.idAcordo}`)}
            >
              <td className="cell-number">{acordo.idAcordo}</td>
              <td className="cell-number">{acordo.nrAcordo}</td>
              <td>{acordo.tipoDescricao}</td>
              <td>
                <StatusBadge codigo={acordo.txSituacao} descricao={acordo.situacaoDescricao} />
              </td>
              <td style={{ textAlign: 'center' }}>
                <VigenciaBadge acordo={acordo} todos={acordos} />
              </td>
              <td className="cell-number">{acordo.cdEmpresa}</td>
              <td>{formatDate(acordo.dtAcordoInicio)}</td>
              <td>{formatDate(acordo.dtAcordoFim)}</td>
              <td>{formatDateTime(acordo.dtCadastro)}</td>
              <td>{formatDateTime(acordo.dtManutencao)}</td>
            </tr>
          ))}
          {acordosFiltrados.length === 0 && (
            <tr>
              <td colSpan={10} className="empty-cell">
                Nenhum acordo encontrado
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {confirmacao && (
        <div className="modal-overlay">
          <div className="modal-box">
            <div className="modal-title">Confirmação</div>
            <div className="modal-msg">{confirmacao.msg}</div>
            <div className="modal-actions">
              <button className="btn-modal-cancel" onClick={() => setConfirmacao(null)}>Cancelar</button>
              <button className="btn-modal-confirm" onClick={confirmacao.onOk}>Confirmar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

function parseLocalDate(dt: string): string {
  // Retorna YYYY-MM-DD sem conversão de timezone
  return dt.substring(0, 10);
}

function isVigente(acordo: AcordoComercial): boolean {
  if (!acordo.dtAcordoInicio || !acordo.dtAcordoFim) return false;
  if (acordo.txSituacao === 'X' || acordo.txSituacao === 'E') return false;
  const hoje = new Date();
  const hojeStr = `${hoje.getFullYear()}-${String(hoje.getMonth() + 1).padStart(2, '0')}-${String(hoje.getDate()).padStart(2, '0')}`;
  const inicio = parseLocalDate(acordo.dtAcordoInicio);
  const fim = parseLocalDate(acordo.dtAcordoFim);
  return hojeStr >= inicio && hojeStr <= fim;
}

function getDuracaoDias(acordo: AcordoComercial): number {
  if (!acordo.dtAcordoInicio || !acordo.dtAcordoFim) return Infinity;
  const inicio = new Date(parseLocalDate(acordo.dtAcordoInicio) + 'T12:00:00');
  const fim = new Date(parseLocalDate(acordo.dtAcordoFim) + 'T12:00:00');
  return (fim.getTime() - inicio.getTime()) / (1000 * 60 * 60 * 24);
}

function periodosSeOverpodem(a: AcordoComercial, b: AcordoComercial): boolean {
  if (!a.dtAcordoInicio || !a.dtAcordoFim || !b.dtAcordoInicio || !b.dtAcordoFim) return false;
  const aIni = parseLocalDate(a.dtAcordoInicio);
  const aFim = parseLocalDate(a.dtAcordoFim);
  const bIni = parseLocalDate(b.dtAcordoInicio);
  const bFim = parseLocalDate(b.dtAcordoFim);
  return aIni <= bFim && bIni <= aFim;
}

function getVigenciaStatus(acordo: AcordoComercial, todos: AcordoComercial[]): 'vigente' | 'sobreposto' | 'nao-vigente' {
  if (!isVigente(acordo)) return 'nao-vigente';

  const tipo = (acordo.txTipo || '') + (acordo.txSubtipo || '');
  const duracao = getDuracaoDias(acordo);

  // Verifica se existe outro acordo vigente do mesmo tipo/empresa com período mais curto que se sobrepõe
  const sobreposto = todos.some(outro => {
    if (outro.idAcordo === acordo.idAcordo) return false;
    if (!isVigente(outro)) return false;
    const outroTipo = (outro.txTipo || '') + (outro.txSubtipo || '');
    if (outroTipo !== tipo) return false;
    if (outro.cdEmpresa !== acordo.cdEmpresa) return false;
    if (!periodosSeOverpodem(acordo, outro)) return false;
    return getDuracaoDias(outro) < duracao;
  });

  return sobreposto ? 'sobreposto' : 'vigente';
}

const BADGE_STYLES: Record<string, { bg: string; color: string; label: string }> = {
  'vigente':     { bg: '#166534', color: '#bbf7d0', label: 'Sim' },
  'sobreposto':  { bg: '#92400e', color: '#fde68a', label: 'Sobreposto' },
  'nao-vigente': { bg: '#7f1d1d', color: '#fecaca', label: 'Não' },
};

const VigenciaBadge: React.FC<{ acordo: AcordoComercial; todos: AcordoComercial[] }> = ({ acordo, todos }) => {
  const status = getVigenciaStatus(acordo, todos);
  const style = BADGE_STYLES[status];
  return (
    <span style={{
      padding: '2px 10px',
      borderRadius: '12px',
      fontSize: '0.8rem',
      fontWeight: 600,
      background: style.bg,
      color: style.color,
    }}>
      {style.label}
    </span>
  );
};

function formatDate(dt: string | null): string {
  if (!dt) return '-';
  // Parsear direto da string ISO (YYYY-MM-DD) para evitar problema de timezone
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
