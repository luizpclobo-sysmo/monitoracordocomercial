import React from 'react';

interface StatusBadgeProps {
  codigo: string | null;
  descricao?: string | null;
  tipo?: 'situacao' | 'etapa';
}

const SITUACAO_COLORS: Record<string, { bg: string; text: string; label: string }> = {
  D: { bg: 'var(--color-gray-bg)', text: 'var(--color-gray)', label: 'Digitando' },
  F: { bg: 'var(--color-blue-bg)', text: 'var(--color-blue)', label: 'Digitação finalizada' },
  U: { bg: 'var(--color-indigo-bg)', text: 'var(--color-indigo)', label: 'Usado nas Compras' },
  P: { bg: 'var(--color-amber-bg)', text: 'var(--color-amber)', label: 'Parcialmente Recebido' },
  X: { bg: 'var(--color-green-bg)', text: 'var(--color-green)', label: 'Finalizado' },
  E: { bg: 'var(--color-red-bg)', text: 'var(--color-red)', label: 'Excluído' },
};

const ETAPA_COLORS: Record<string, { bg: string; text: string; label: string }> = {
  A: { bg: 'var(--color-amber-bg)', text: 'var(--color-amber)', label: 'Aguardando' },
  AP: { bg: 'var(--color-blue-bg)', text: 'var(--color-blue)', label: 'Aguardando Produtos' },
  PP: { bg: 'var(--color-indigo-bg)', text: 'var(--color-indigo)', label: 'Processando Produtos' },
  AF: { bg: 'var(--color-blue-bg)', text: 'var(--color-blue)', label: 'Aguardando Fechamento' },
  PF: { bg: 'var(--color-indigo-bg)', text: 'var(--color-indigo)', label: 'Processando Fechamento' },
  AV: { bg: 'var(--color-blue-bg)', text: 'var(--color-blue)', label: 'Aguardando Verba' },
  PV: { bg: 'var(--color-indigo-bg)', text: 'var(--color-indigo)', label: 'Processando Verba' },
  X: { bg: 'var(--color-green-bg)', text: 'var(--color-green)', label: 'Finalizado' },
  E: { bg: 'var(--color-red-bg)', text: 'var(--color-red)', label: 'Erro' },
};

export const StatusBadge: React.FC<StatusBadgeProps> = ({ codigo, descricao, tipo = 'situacao' }) => {
  const code = codigo?.trim() || '?';
  const map = tipo === 'situacao' ? SITUACAO_COLORS : ETAPA_COLORS;
  const style = map[code] || { bg: 'var(--color-gray-bg)', text: 'var(--color-gray)', label: code };
  const label = descricao || style.label;

  return (
    <span
      className="status-badge"
      style={{ backgroundColor: style.bg, color: style.text }}
    >
      {label}
    </span>
  );
};
