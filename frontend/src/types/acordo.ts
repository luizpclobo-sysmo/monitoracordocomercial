export interface EmpresaFiltro {
  cod: number;
  nome: string;
}

// Matches ProdutoVerbaDTO (produtos-verba endpoint)
export interface ProdutoVerba {
  cdEmpresa: number;
  cdProduto: number;
  txProdutoDescricao: string | null;
  vlVerbaAntecipacao: number | null;
  vlVerbaSellOut: number | null;
  vlVerbaSellIn: number | null;
  vlVerbaContaComercial: number | null;
  nrAcordoVigente: number;
  vlUnidadeAcordo: number | null;
  pcUnidadeAcordo: number | null;
  txTipoAcordo: string | null;
  txSubtipoAcordo: string | null;
  vlPrecoVenda: number | null;
  tipoAcordoDescricao: string;
  vigente: boolean;
  vlVerbaEsperada: number | null;
  correto: boolean;
  motivoInconsistencia: string | null;
}

// Matches AcordoComercial entity (list endpoint)
export interface AcordoComercial {
  idAcordo: number;
  nrAcordo: number;
  txSituacao: string;
  txTipo: string;
  txSubtipo: string;
  cdEmpresa: number;
  cdUsuario: number;
  dtAcordoInicio: string | null;
  dtAcordoFim: string | null;
  dtCadastro: string | null;
  dtManutencao: string | null;
  situacaoDescricao: string;
  tipoDescricao: string;
}

// Matches AcordoDetalhadoDTO (detail endpoint)
export interface AcordoDetalhado {
  idAcordo: number;
  nrAcordo: number;
  txSituacao: string;
  situacaoDescricao: string;
  txTipo: string;
  txSubtipo: string;
  cdEmpresa: number;
  cdUsuario: number;
  dtAcordoInicio: string | null;
  dtAcordoFim: string | null;
  dtCadastro: string | null;
  dtManutencao: string | null;
  sellOutTotal: SellOutTotal | null;
  itens: SellOutItem[];
  empresas: Empresa[];
  transacionador: Transacionador | null;
  processamentos: Processamento[];
  processamentoItens: ProcessamentoItem[];
  periodos: Periodo[];
  lotes: Lote[];
  prestacoes: Prestacao[];
  distribuicoes: Distribuicao[];
  integracaoDocumentos: IntegracaoDocumento[];
  vendas: VendaDTO[];
}

export interface SellOutTotal {
  cdAcordo: number;
  flFechamentoManual: string | null;
  cdFormaPagamento: number;
  cdCondicaoPagamento: number;
  cdBanco: number;
  txControle: string | null;
  txMensagem: string | null;
  flAtribuirVerbaContaComercial: string | null;
}

export interface SellOutItem {
  idCodigo: number;
  cdAcordo: number;
  cdProduto: number;
  txProdutoDescricao: string | null;
  pcUnidade: number | null;
  vlUnidade: number | null;
}

export interface Empresa {
  cdAcordo: number;
  cdEmpresa: number;
}

export interface Transacionador {
  cdAcordo: number;
  cdFornecedor: number;
  cdComprador: number;
}

export interface Processamento {
  idSequencial: number;
  cdAcordo: number;
  cdTipo: string | null;
  cdEtapa: string | null;
  etapaDescricao: string | null;
  qtTentativas: number;
  dtProcessamento: string | null;
  txObservacao: string | null;
}

export interface ProcessamentoItem {
  idSequencial: number;
  cdAcordo: number;
  cdEmpresa: number;
  cdProduto: number;
  cdTipo: string | null;
  cdEtapa: string | null;
  etapaDescricao: string | null;
}

export interface Periodo {
  idSequencial: number;
  cdAcordo: number;
  cdEmpresa: number;
  cdProduto: number;
  dtInicio: string | null;
  dtFim: string | null;
}

export interface Lote {
  cdLote: number;
  cdAcordo: number;
  dtInicial: string | null;
  dtFinal: string | null;
  vlRecebido: number | null;
  cdEmpresa: number;
  dtCadastro: string | null;
  dtManutencao: string | null;
  cdUsuario: number;
}

export interface Prestacao {
  cdSequencial: number;
  nrAcordo: number;
  cdEmpresaAcordo: number;
  cdFornecedorAcordo: number;
  cdEmpresaDocumento: number;
  nrDocumento: number;
  nrPrestacao: number;
  cdEmpresaNota: number;
  nrNota: number;
  txSerieNota: string | null;
  cdTransacionadorNota: number;
  dtNota: string | null;
  vlPrestacao: number | null;
  dtVencimento: string | null;
  vlTotalDocumento: number | null;
  flUtilizado: string | null;
  cdFormaPagamento: number;
  cdCondicaoPagamento: number;
  cdBanco: number;
  cdOrigem: number;
}

export interface Distribuicao {
  idDistribuicao: number;
  cdAcordo: number;
  cdOrigem: number;
  cdEmpresa: number;
  vlDistribuido: number | null;
  pcDistribuido: number | null;
}

export interface IntegracaoDocumento {
  idSequencial: number;
  cdEmpresa: number;
  txTipo: string | null;
  txSerie: string | null;
  cdFornecedor: number;
  nrNumero: number;
}

export interface VendaPeriodoDTO {
  cdEmpresa: number;
  cdProduto: number;
  txProdutoDescricao: string | null;
  pcUnidade: number | null;
  vlUnidade: number | null;
  dtInicio: string | null;
  dtFim: string | null;
  quantidadeVendida: number | null;
  valorTotal: number | null;
  quantidadeNotas: number;
}

export interface VendaDTO {
  cdEmpresa: number;
  cdProduto: number;
  nrNumero: string | null;
  txSerie: string | null;
  dtRegistro: string | null;
  quantidade: number | null;
  valorTotal: number | null;
  op1: string | null;
  op2: string | null;
  mod: string | null;
  cfl: number;
  valorMercadoria: number | null;
}
