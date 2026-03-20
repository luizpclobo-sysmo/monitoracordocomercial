import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AcordoComercial, EmpresaFiltro } from '../types/acordo';
import { listarAcordos, listarEmpresas } from '../services/api';
import { StatusBadge } from './StatusBadge';

interface PeriodoAgrupado {
  dtInicio: string;
  dtFim: string;
  acordos: AcordoComercial[];
}

export const AcordoPeriodoList: React.FC = () => {
  const [acordos, setAcordos] = useState<AcordoComercial[]>([]);
  const [empresas, setEmpresas] = useState<EmpresaFiltro[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const navigate = useNavigate();

  const [selEmpresa, setSelEmpresa] = useState(() => localStorage.getItem('filtroPeriodoEmpresa') || '');
  const [selTipo, setSelTipo] = useState(() => localStorage.getItem('filtroPeriodoTipo') || '');
  const [filtroEmpresa, setFiltroEmpresa] = useState(selEmpresa);
  const [filtroTipo, setFiltroTipo] = useState(selTipo);

  const temAlteracoes = selEmpresa !== filtroEmpresa || selTipo !== filtroTipo;

  const aplicarFiltros = () => {
    localStorage.setItem('filtroPeriodoEmpresa', selEmpresa);
    localStorage.setItem('filtroPeriodoTipo', selTipo);
    setFiltroEmpresa(selEmpresa);
    setFiltroTipo(selTipo);
  };

  useEffect(() => {
    Promise.all([listarAcordos(), listarEmpresas()])
      .then(([ac, emp]) => { setAcordos(ac); setEmpresas(emp); })
      .catch((e) => setErro('Erro ao carregar dados: ' + e.message))
      .finally(() => setLoading(false));
    const interval = setInterval(() => {
      listarAcordos().then(setAcordos).catch(() => {});
    }, 10000);
    return () => clearInterval(interval);
  }, []);

  const acordosFiltrados = acordos
    .filter((a) => filtroEmpresa === '' || a.cdEmpresa === Number(filtroEmpresa))
    .filter((a) => filtroTipo === '' || a.txTipo + a.txSubtipo === filtroTipo);

  const periodos = agruparPorPeriodo(acordosFiltrados);

  if (loading) return <div className="loading">Carregando...</div>;
  if (erro) return <div className="error-message">{erro}</div>;

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Acordos por Periodo</h1>
        <select className="filtro-tipo" value={selEmpresa} onChange={(e) => setSelEmpresa(e.target.value)}>
          <option value="">Todas empresas</option>
          {empresas.map((emp) => (
            <option key={emp.cod} value={emp.cod}>{emp.cod} - {emp.nome}</option>
          ))}
        </select>
        <select className="filtro-tipo" value={selTipo} onChange={(e) => setSelTipo(e.target.value)}>
          <option value="">Todos tipos</option>
          <option value="SO">Sell-Out</option>
          <option value="SI">Sell-In</option>
          <option value="BP">Bonificacao de Produto</option>
          <option value="AV">Antecipacao de Verba</option>
        </select>
        <button className="btn-aplicar" onClick={aplicarFiltros} disabled={!temAlteracoes}>
          Aplicar
        </button>
        <span className="record-count">{periodos.length} periodo(s), {acordosFiltrados.length} acordo(s)</span>
      </div>

      <table className="data-table">
        <thead>
          <tr>
            <th>Periodo</th>
            <th>Nr. Acordo</th>
            <th>Tipo</th>
            <th>Situacao</th>
            <th>Empresa</th>
          </tr>
        </thead>
        <tbody>
          {periodos.map((p) =>
            p.acordos.map((acordo, idx) => (
              <tr
                key={acordo.idAcordo}
                className="clickable-row"
                onClick={() => navigate(`/acordos/${acordo.idAcordo}`)}
              >
                {idx === 0 && (
                  <td rowSpan={p.acordos.length} className="cell-periodo">
                    {formatDate(p.dtInicio)} / {formatDate(p.dtFim)}
                  </td>
                )}
                <td className="cell-number">{acordo.nrAcordo}</td>
                <td>{acordo.tipoDescricao}</td>
                <td>
                  <StatusBadge codigo={acordo.txSituacao} descricao={acordo.situacaoDescricao} />
                </td>
                <td className="cell-number">{acordo.cdEmpresa}</td>
              </tr>
            ))
          )}
          {periodos.length === 0 && (
            <tr>
              <td colSpan={5} className="empty-cell">Nenhum acordo encontrado</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

function agruparPorPeriodo(acordos: AcordoComercial[]): PeriodoAgrupado[] {
  const map = new Map<string, AcordoComercial[]>();

  for (const acordo of acordos) {
    const inicio = acordo.dtAcordoInicio?.substring(0, 10) || '';
    const fim = acordo.dtAcordoFim?.substring(0, 10) || '';
    const chave = `${inicio}|${fim}`;

    if (!map.has(chave)) map.set(chave, []);
    map.get(chave)!.push(acordo);
  }

  return Array.from(map.entries())
    .map(([chave, acordos]) => {
      const [dtInicio, dtFim] = chave.split('|');
      return { dtInicio, dtFim, acordos: acordos.sort((a, b) => a.nrAcordo - b.nrAcordo) };
    })
    .sort((a, b) => a.dtInicio.localeCompare(b.dtInicio));
}

function formatDate(dt: string): string {
  if (!dt) return '-';
  const parts = dt.split('-');
  if (parts.length === 3) return `${parts[2]}/${parts[1]}/${parts[0]}`;
  return dt;
}
