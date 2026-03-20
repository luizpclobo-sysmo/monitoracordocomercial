import React from 'react';
import { Processamento, ProcessamentoItem } from '../types/acordo';

interface PipelineProps {
  processamentos: Processamento[];
  processamentoItens: ProcessamentoItem[];
}

const STEPS_ABERTURA = [
  { code: 'A', label: 'Aguardando' },
  { code: 'AP', label: 'Ag. Produtos' },
  { code: 'PP', label: 'Proc. Produtos' },
];

const STEPS_FECHAMENTO = [
  { code: 'AF', label: 'Ag. Fechamento' },
  { code: 'PF', label: 'Proc. Fechamento' },
  { code: 'AV', label: 'Ag. Verba' },
  { code: 'PV', label: 'Proc. Verba' },
  { code: 'X', label: 'Finalizado' },
];

const ALL_STEPS = [...STEPS_ABERTURA, ...STEPS_FECHAMENTO];

function getStepStatus(
  stepCode: string,
  currentEtapa: string | null,
  hasError: boolean
): 'completed' | 'current' | 'current-error' | 'pending' {
  if (!currentEtapa) return 'pending';

  const currentIdx = ALL_STEPS.findIndex((s) => s.code === currentEtapa);
  const stepIdx = ALL_STEPS.findIndex((s) => s.code === stepCode);

  if (stepIdx < 0 || currentIdx < 0) return 'pending';

  if (stepIdx < currentIdx) return 'completed';
  if (stepIdx === currentIdx) return hasError ? 'current-error' : 'current';
  return 'pending';
}

function isAberturaCompleta(etapa: string | null): boolean {
  if (!etapa) return false;
  const idx = ALL_STEPS.findIndex((s) => s.code === etapa);
  const ppIdx = ALL_STEPS.findIndex((s) => s.code === 'PP');
  return idx > ppIdx;
}

const StepTrack: React.FC<{
  steps: { code: string; label: string }[];
  currentEtapa: string | null;
  hasError: boolean;
}> = ({ steps, currentEtapa, hasError }) => (
  <div className="pipeline-track">
    {steps.map((step, idx) => {
      const status = getStepStatus(step.code, currentEtapa, hasError);
      return (
        <React.Fragment key={step.code}>
          {idx > 0 && (
            <div className={`pipeline-connector ${status === 'completed' ? 'completed' : ''}`} />
          )}
          <div className={`pipeline-step ${status}`}>
            <div className="pipeline-dot" />
            <span className="pipeline-step-label">{step.label}</span>
          </div>
        </React.Fragment>
      );
    })}
  </div>
);

export const ProcessingPipeline: React.FC<PipelineProps> = ({
  processamentos,
  processamentoItens,
}) => {
  const latestProc = processamentos.length > 0 ? processamentos[0] : null;
  const currentEtapaCab = latestProc?.cdEtapa || null;
  const hasErrorCab = latestProc?.cdEtapa === 'E';

  const itemEtapas = processamentoItens
    .map((pi) => pi.cdEtapa)
    .filter((e): e is string => e !== null);
  const uniqueItemEtapas = [...new Set(itemEtapas)];

  let currentEtapaItem: string | null = null;
  if (uniqueItemEtapas.length > 0) {
    let minIdx = ALL_STEPS.length;
    for (const etapa of uniqueItemEtapas) {
      const idx = ALL_STEPS.findIndex((s) => s.code === etapa);
      if (idx >= 0 && idx < minIdx) {
        minIdx = idx;
        currentEtapaItem = etapa;
      }
    }
  }
  const hasErrorItem = uniqueItemEtapas.includes('E');

  const aberturaCompletaCab = isAberturaCompleta(currentEtapaCab);
  const aberturaCompletaItem = isAberturaCompleta(currentEtapaItem);

  return (
    <div className="pipeline-container">
      {/* === ABERTURA === */}
      <div className="pipeline-group">
        <h3 className="pipeline-group-title">
          Abertura
          <ProcessamentoBadge processamentos={processamentos} tipo="A" />
        </h3>

        <div className="pipeline-section">
          <h4 className="pipeline-label">Cabecalho</h4>
          <StepTrack steps={STEPS_ABERTURA} currentEtapa={currentEtapaCab} hasError={hasErrorCab} />
          {latestProc && !aberturaCompletaCab && (
            <div className="pipeline-info">
              Etapa atual: <strong>{latestProc.etapaDescricao || latestProc.cdEtapa}</strong>
              {latestProc.qtTentativas > 0 && (
                <span className="pipeline-attempts"> | Tentativas: {latestProc.qtTentativas}</span>
              )}
              {latestProc.txObservacao && (
                <span className="pipeline-obs"> | {latestProc.txObservacao}</span>
              )}
            </div>
          )}
        </div>

        {processamentoItens.length > 0 && (
          <div className="pipeline-section">
            <h4 className="pipeline-label">Itens</h4>
            <StepTrack steps={STEPS_ABERTURA} currentEtapa={currentEtapaItem} hasError={hasErrorItem} />
            <div className="pipeline-item-detail">
              <table className="data-table compact">
                <thead>
                  <tr>
                    <th>Empresa</th>
                    <th>Produto</th>
                    <th>Etapa</th>
                  </tr>
                </thead>
                <tbody>
                  {processamentoItens.map((pi, idx) => (
                    <tr key={idx}>
                      <td>{pi.cdEmpresa}</td>
                      <td>{pi.cdProduto}</td>
                      <td>
                        <span className={`etapa-tag ${pi.cdEtapa === 'X' ? 'finalizado' : pi.cdEtapa === 'E' ? 'erro' : ''}`}>
                          {pi.etapaDescricao || pi.cdEtapa}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>

      {/* === FECHAMENTO === */}
      <div className="pipeline-group">
        <h3 className="pipeline-group-title">
          Fechamento
          <ProcessamentoBadge processamentos={processamentos} tipo="F" />
        </h3>

        <div className="pipeline-section">
          <h4 className="pipeline-label">Cabecalho</h4>
          <StepTrack steps={STEPS_FECHAMENTO} currentEtapa={currentEtapaCab} hasError={hasErrorCab} />
          {latestProc && aberturaCompletaCab && (
            <div className="pipeline-info">
              Etapa atual: <strong>{latestProc.etapaDescricao || latestProc.cdEtapa}</strong>
              {latestProc.qtTentativas > 0 && (
                <span className="pipeline-attempts"> | Tentativas: {latestProc.qtTentativas}</span>
              )}
              {latestProc.txObservacao && (
                <span className="pipeline-obs"> | {latestProc.txObservacao}</span>
              )}
            </div>
          )}
        </div>
      </div>

      {/* === HISTORICO === */}
      {processamentos.length > 1 && (
        <div className="pipeline-section">
          <h4 className="pipeline-label">Historico de Processamento</h4>
          <table className="data-table compact">
            <thead>
              <tr>
                <th>Data</th>
                <th>Etapa</th>
                <th>Tipo</th>
                <th>Tentativas</th>
                <th>Observacao</th>
              </tr>
            </thead>
            <tbody>
              {processamentos.map((p, idx) => (
                <tr key={idx}>
                  <td>{formatDateTime(p.dtProcessamento)}</td>
                  <td>{p.etapaDescricao || p.cdEtapa}</td>
                  <td>{p.cdTipo}</td>
                  <td>{p.qtTentativas}</td>
                  <td className="obs-cell">{p.txObservacao || '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

const ProcessamentoBadge: React.FC<{ processamentos: Processamento[]; tipo: string }> = ({ processamentos, tipo }) => {
  const proc = processamentos.find(p => p.cdTipo === tipo);
  const dt = proc?.dtProcessamento;
  const temData = dt && dt !== '-';
  return (
    <span style={{
      marginLeft: '10px',
      padding: '2px 10px',
      borderRadius: '12px',
      fontSize: '0.75rem',
      fontWeight: 600,
      background: temData ? '#166534' : '#7f1d1d',
      color: temData ? '#bbf7d0' : '#fecaca',
    }}>
      {temData ? formatDateTime(dt) : 'Não processado'}
    </span>
  );
};

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
