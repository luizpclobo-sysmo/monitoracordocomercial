import axios from 'axios';
import { AcordoComercial, AcordoDetalhado, EmpresaFiltro, ProdutoVerba, VendaDTO, VendaPeriodoDTO } from '../types/acordo';

const api = axios.create({
  baseURL: '/api',
});

export async function listarAcordos(): Promise<AcordoComercial[]> {
  const response = await api.get<AcordoComercial[]>('/acordos');
  return response.data;
}

export async function buscarAcordoDetalhado(id: number): Promise<AcordoDetalhado> {
  const response = await api.get<AcordoDetalhado>(`/acordos/${id}`);
  return response.data;
}

export async function buscarVendas(id: number): Promise<VendaDTO[]> {
  const response = await api.get<VendaDTO[]>(`/acordos/${id}/vendas`);
  return response.data;
}

export async function buscarVendasPorPeriodo(id: number): Promise<VendaPeriodoDTO[]> {
  const response = await api.get<VendaPeriodoDTO[]>(`/acordos/${id}/vendas-periodo`);
  return response.data;
}

export async function listarEmpresas(): Promise<EmpresaFiltro[]> {
  const response = await api.get<EmpresaFiltro[]>('/acordos/empresas');
  return response.data;
}

export async function listarProdutosVerba(): Promise<ProdutoVerba[]> {
  const response = await api.get<ProdutoVerba[]>('/acordos/produtos-verba');
  return response.data;
}

export async function limparTudo(): Promise<{ sucesso: boolean; mensagem: string }> {
  const response = await api.delete('/acordos/limpar-tudo');
  return response.data;
}

export async function atualizarAcordoPeriodo(id: number, dtAcordoInicio: string, dtAcordoFim: string): Promise<void> {
  await api.put(`/acordos/${id}/periodo`, { dtAcordoInicio, dtAcordoFim });
}

export async function atualizarPeriodo(idSequencial: number, dtInicio: string, dtFim: string): Promise<void> {
  await api.put(`/acordos/periodos/${idSequencial}`, { dtInicio, dtFim });
}
