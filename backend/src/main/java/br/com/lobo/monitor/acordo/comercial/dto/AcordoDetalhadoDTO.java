package br.com.lobo.monitor.acordo.comercial.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AcordoDetalhadoDTO {

    // === Acordo Header (TB_ACORDOCOMERCIAL) ===
    private BigDecimal idAcordo;
    private int nrAcordo;
    private String txSituacao;
    private String situacaoDescricao;
    private String txTipo;
    private String txSubtipo;
    private int cdEmpresa;
    private int cdUsuario;
    private LocalDate dtAcordoInicio;
    private LocalDate dtAcordoFim;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;

    // === Sell Out Total Config (TB_ACORDOCOMERCIALSELLOUTTOTAL) ===
    private SellOutTotalDTO sellOutTotal;

    // === Itens/Produtos (TB_ACORDOCOMERCIALSELLOUTITEM) ===
    private List<SellOutItemDTO> itens = new ArrayList<>();

    // === Empresas (TB_ACORDOCOMERCIALEMPRESA) ===
    private List<EmpresaDTO> empresas = new ArrayList<>();

    // === Transacionador (TB_ACORDOCOMERCIALTRANSACIONADOR) ===
    private TransacionadorDTO transacionador;

    // === Processamento Cabecalho (TB_ACORDOCOMERCIALSELLOUTPROCESSAMENTO) ===
    private List<ProcessamentoDTO> processamentos = new ArrayList<>();

    // === Processamento Itens (TB_ACORDOCOMERCIALSELLOUTITEMPROCESSAMENTO) ===
    private List<ProcessamentoItemDTO> processamentoItens = new ArrayList<>();

    // === Periodos/Fragmentos (TB_ACORDOCOMERCIALSELLOUTPERIODO) ===
    private List<PeriodoDTO> periodos = new ArrayList<>();

    // === Lotes (TB_ACORDOCOMERCIALSELLOUT) ===
    private List<LoteDTO> lotes = new ArrayList<>();

    // === Prestacoes (TB_ACORDOCOMERCIALPRESTACAO) ===
    private List<PrestacaoDTO> prestacoes = new ArrayList<>();

    // === Distribuicao (TB_ACORDOCOMERCIALDISTRIBUICAO) ===
    private List<DistribuicaoDTO> distribuicoes = new ArrayList<>();

    // === Integracao Documento (TB_ACORDOCOMERCIALINTEGRACAODOCUMENTO) ===
    private List<IntegracaoDocumentoDTO> integracaoDocumentos = new ArrayList<>();

    // === Vendas (GCEITM01 + GCENFS01) ===
    private List<VendaDTO> vendas = new ArrayList<>();

    // --- Getters and Setters ---

    public BigDecimal getIdAcordo() { return idAcordo; }
    public void setIdAcordo(BigDecimal idAcordo) { this.idAcordo = idAcordo; }

    public int getNrAcordo() { return nrAcordo; }
    public void setNrAcordo(int nrAcordo) { this.nrAcordo = nrAcordo; }

    public String getTxSituacao() { return txSituacao; }
    public void setTxSituacao(String txSituacao) { this.txSituacao = txSituacao; }

    public String getSituacaoDescricao() { return situacaoDescricao; }
    public void setSituacaoDescricao(String situacaoDescricao) { this.situacaoDescricao = situacaoDescricao; }

    public String getTxTipo() { return txTipo; }
    public void setTxTipo(String txTipo) { this.txTipo = txTipo; }

    public String getTxSubtipo() { return txSubtipo; }
    public void setTxSubtipo(String txSubtipo) { this.txSubtipo = txSubtipo; }

    public int getCdEmpresa() { return cdEmpresa; }
    public void setCdEmpresa(int cdEmpresa) { this.cdEmpresa = cdEmpresa; }

    public int getCdUsuario() { return cdUsuario; }
    public void setCdUsuario(int cdUsuario) { this.cdUsuario = cdUsuario; }

    public LocalDate getDtAcordoInicio() { return dtAcordoInicio; }
    public void setDtAcordoInicio(LocalDate dtAcordoInicio) { this.dtAcordoInicio = dtAcordoInicio; }

    public LocalDate getDtAcordoFim() { return dtAcordoFim; }
    public void setDtAcordoFim(LocalDate dtAcordoFim) { this.dtAcordoFim = dtAcordoFim; }

    public LocalDateTime getDtCadastro() { return dtCadastro; }
    public void setDtCadastro(LocalDateTime dtCadastro) { this.dtCadastro = dtCadastro; }

    public LocalDateTime getDtManutencao() { return dtManutencao; }
    public void setDtManutencao(LocalDateTime dtManutencao) { this.dtManutencao = dtManutencao; }

    public SellOutTotalDTO getSellOutTotal() { return sellOutTotal; }
    public void setSellOutTotal(SellOutTotalDTO sellOutTotal) { this.sellOutTotal = sellOutTotal; }

    public List<SellOutItemDTO> getItens() { return itens; }
    public void setItens(List<SellOutItemDTO> itens) { this.itens = itens; }

    public List<EmpresaDTO> getEmpresas() { return empresas; }
    public void setEmpresas(List<EmpresaDTO> empresas) { this.empresas = empresas; }

    public TransacionadorDTO getTransacionador() { return transacionador; }
    public void setTransacionador(TransacionadorDTO transacionador) { this.transacionador = transacionador; }

    public List<ProcessamentoDTO> getProcessamentos() { return processamentos; }
    public void setProcessamentos(List<ProcessamentoDTO> processamentos) { this.processamentos = processamentos; }

    public List<ProcessamentoItemDTO> getProcessamentoItens() { return processamentoItens; }
    public void setProcessamentoItens(List<ProcessamentoItemDTO> processamentoItens) { this.processamentoItens = processamentoItens; }

    public List<PeriodoDTO> getPeriodos() { return periodos; }
    public void setPeriodos(List<PeriodoDTO> periodos) { this.periodos = periodos; }

    public List<LoteDTO> getLotes() { return lotes; }
    public void setLotes(List<LoteDTO> lotes) { this.lotes = lotes; }

    public List<PrestacaoDTO> getPrestacoes() { return prestacoes; }
    public void setPrestacoes(List<PrestacaoDTO> prestacoes) { this.prestacoes = prestacoes; }

    public List<DistribuicaoDTO> getDistribuicoes() { return distribuicoes; }
    public void setDistribuicoes(List<DistribuicaoDTO> distribuicoes) { this.distribuicoes = distribuicoes; }

    public List<IntegracaoDocumentoDTO> getIntegracaoDocumentos() { return integracaoDocumentos; }
    public void setIntegracaoDocumentos(List<IntegracaoDocumentoDTO> integracaoDocumentos) { this.integracaoDocumentos = integracaoDocumentos; }

    public List<VendaDTO> getVendas() { return vendas; }
    public void setVendas(List<VendaDTO> vendas) { this.vendas = vendas; }

    // === Nested DTOs ===

    public static class SellOutTotalDTO {
        private BigDecimal cdAcordo;
        private String flFechamentoManual;
        private int cdFormaPagamento;
        private int cdCondicaoPagamento;
        private int cdBanco;
        private String txControle;
        private String txMensagem;
        private String flAtribuirVerbaContaComercial;

        public BigDecimal getCdAcordo() { return cdAcordo; }
        public void setCdAcordo(BigDecimal cdAcordo) { this.cdAcordo = cdAcordo; }

        public String getFlFechamentoManual() { return flFechamentoManual; }
        public void setFlFechamentoManual(String flFechamentoManual) { this.flFechamentoManual = flFechamentoManual; }

        public int getCdFormaPagamento() { return cdFormaPagamento; }
        public void setCdFormaPagamento(int cdFormaPagamento) { this.cdFormaPagamento = cdFormaPagamento; }

        public int getCdCondicaoPagamento() { return cdCondicaoPagamento; }
        public void setCdCondicaoPagamento(int cdCondicaoPagamento) { this.cdCondicaoPagamento = cdCondicaoPagamento; }

        public int getCdBanco() { return cdBanco; }
        public void setCdBanco(int cdBanco) { this.cdBanco = cdBanco; }

        public String getTxControle() { return txControle; }
        public void setTxControle(String txControle) { this.txControle = txControle; }

        public String getTxMensagem() { return txMensagem; }
        public void setTxMensagem(String txMensagem) { this.txMensagem = txMensagem; }

        public String getFlAtribuirVerbaContaComercial() { return flAtribuirVerbaContaComercial; }
        public void setFlAtribuirVerbaContaComercial(String flAtribuirVerbaContaComercial) { this.flAtribuirVerbaContaComercial = flAtribuirVerbaContaComercial; }
    }

    public static class SellOutItemDTO {
        private BigDecimal idCodigo;
        private BigDecimal cdAcordo;
        private int cdProduto;
        private String txProdutoDescricao;
        private BigDecimal pcUnidade;
        private BigDecimal vlUnidade;

        public BigDecimal getIdCodigo() { return idCodigo; }
        public void setIdCodigo(BigDecimal idCodigo) { this.idCodigo = idCodigo; }

        public BigDecimal getCdAcordo() { return cdAcordo; }
        public void setCdAcordo(BigDecimal cdAcordo) { this.cdAcordo = cdAcordo; }

        public int getCdProduto() { return cdProduto; }
        public void setCdProduto(int cdProduto) { this.cdProduto = cdProduto; }

        public String getTxProdutoDescricao() { return txProdutoDescricao; }
        public void setTxProdutoDescricao(String txProdutoDescricao) { this.txProdutoDescricao = txProdutoDescricao; }

        public BigDecimal getPcUnidade() { return pcUnidade; }
        public void setPcUnidade(BigDecimal pcUnidade) { this.pcUnidade = pcUnidade; }

        public BigDecimal getVlUnidade() { return vlUnidade; }
        public void setVlUnidade(BigDecimal vlUnidade) { this.vlUnidade = vlUnidade; }
    }

    public static class EmpresaDTO {
        private BigDecimal cdAcordo;
        private int cdEmpresa;

        public BigDecimal getCdAcordo() { return cdAcordo; }
        public void setCdAcordo(BigDecimal cdAcordo) { this.cdAcordo = cdAcordo; }

        public int getCdEmpresa() { return cdEmpresa; }
        public void setCdEmpresa(int cdEmpresa) { this.cdEmpresa = cdEmpresa; }
    }

    public static class TransacionadorDTO {
        private BigDecimal cdAcordo;
        private int cdFornecedor;
        private int cdComprador;

        public BigDecimal getCdAcordo() { return cdAcordo; }
        public void setCdAcordo(BigDecimal cdAcordo) { this.cdAcordo = cdAcordo; }

        public int getCdFornecedor() { return cdFornecedor; }
        public void setCdFornecedor(int cdFornecedor) { this.cdFornecedor = cdFornecedor; }

        public int getCdComprador() { return cdComprador; }
        public void setCdComprador(int cdComprador) { this.cdComprador = cdComprador; }
    }

    public static class ProcessamentoDTO {
        private BigDecimal idSequencial;
        private BigDecimal cdAcordo;
        private String cdTipo;
        private String cdEtapa;
        private String etapaDescricao;
        private int qtTentativas;
        private LocalDateTime dtProcessamento;
        private String txObservacao;

        public BigDecimal getIdSequencial() { return idSequencial; }
        public void setIdSequencial(BigDecimal idSequencial) { this.idSequencial = idSequencial; }

        public BigDecimal getCdAcordo() { return cdAcordo; }
        public void setCdAcordo(BigDecimal cdAcordo) { this.cdAcordo = cdAcordo; }

        public String getCdTipo() { return cdTipo; }
        public void setCdTipo(String cdTipo) { this.cdTipo = cdTipo; }

        public String getCdEtapa() { return cdEtapa; }
        public void setCdEtapa(String cdEtapa) { this.cdEtapa = cdEtapa; }

        public String getEtapaDescricao() { return etapaDescricao; }
        public void setEtapaDescricao(String etapaDescricao) { this.etapaDescricao = etapaDescricao; }

        public int getQtTentativas() { return qtTentativas; }
        public void setQtTentativas(int qtTentativas) { this.qtTentativas = qtTentativas; }

        public LocalDateTime getDtProcessamento() { return dtProcessamento; }
        public void setDtProcessamento(LocalDateTime dtProcessamento) { this.dtProcessamento = dtProcessamento; }

        public String getTxObservacao() { return txObservacao; }
        public void setTxObservacao(String txObservacao) { this.txObservacao = txObservacao; }
    }

    public static class ProcessamentoItemDTO {
        private BigDecimal idSequencial;
        private BigDecimal cdAcordo;
        private int cdEmpresa;
        private int cdProduto;
        private String cdTipo;
        private String cdEtapa;
        private String etapaDescricao;

        public BigDecimal getIdSequencial() { return idSequencial; }
        public void setIdSequencial(BigDecimal idSequencial) { this.idSequencial = idSequencial; }

        public BigDecimal getCdAcordo() { return cdAcordo; }
        public void setCdAcordo(BigDecimal cdAcordo) { this.cdAcordo = cdAcordo; }

        public int getCdEmpresa() { return cdEmpresa; }
        public void setCdEmpresa(int cdEmpresa) { this.cdEmpresa = cdEmpresa; }

        public int getCdProduto() { return cdProduto; }
        public void setCdProduto(int cdProduto) { this.cdProduto = cdProduto; }

        public String getCdTipo() { return cdTipo; }
        public void setCdTipo(String cdTipo) { this.cdTipo = cdTipo; }

        public String getCdEtapa() { return cdEtapa; }
        public void setCdEtapa(String cdEtapa) { this.cdEtapa = cdEtapa; }

        public String getEtapaDescricao() { return etapaDescricao; }
        public void setEtapaDescricao(String etapaDescricao) { this.etapaDescricao = etapaDescricao; }
    }

    public static class PeriodoDTO {
        private BigDecimal idSequencial;
        private BigDecimal cdAcordo;
        private int cdEmpresa;
        private int cdProduto;
        private LocalDate dtInicio;
        private LocalDate dtFim;

        public BigDecimal getIdSequencial() { return idSequencial; }
        public void setIdSequencial(BigDecimal idSequencial) { this.idSequencial = idSequencial; }

        public BigDecimal getCdAcordo() { return cdAcordo; }
        public void setCdAcordo(BigDecimal cdAcordo) { this.cdAcordo = cdAcordo; }

        public int getCdEmpresa() { return cdEmpresa; }
        public void setCdEmpresa(int cdEmpresa) { this.cdEmpresa = cdEmpresa; }

        public int getCdProduto() { return cdProduto; }
        public void setCdProduto(int cdProduto) { this.cdProduto = cdProduto; }

        public LocalDate getDtInicio() { return dtInicio; }
        public void setDtInicio(LocalDate dtInicio) { this.dtInicio = dtInicio; }

        public LocalDate getDtFim() { return dtFim; }
        public void setDtFim(LocalDate dtFim) { this.dtFim = dtFim; }
    }

    public static class LoteDTO {
        private int cdLote;
        private int cdAcordo;
        private LocalDate dtInicial;
        private LocalDate dtFinal;
        private BigDecimal vlRecebido;
        private int cdEmpresa;
        private LocalDateTime dtCadastro;
        private LocalDateTime dtManutencao;
        private int cdUsuario;

        public int getCdLote() { return cdLote; }
        public void setCdLote(int cdLote) { this.cdLote = cdLote; }

        public int getCdAcordo() { return cdAcordo; }
        public void setCdAcordo(int cdAcordo) { this.cdAcordo = cdAcordo; }

        public LocalDate getDtInicial() { return dtInicial; }
        public void setDtInicial(LocalDate dtInicial) { this.dtInicial = dtInicial; }

        public LocalDate getDtFinal() { return dtFinal; }
        public void setDtFinal(LocalDate dtFinal) { this.dtFinal = dtFinal; }

        public BigDecimal getVlRecebido() { return vlRecebido; }
        public void setVlRecebido(BigDecimal vlRecebido) { this.vlRecebido = vlRecebido; }

        public int getCdEmpresa() { return cdEmpresa; }
        public void setCdEmpresa(int cdEmpresa) { this.cdEmpresa = cdEmpresa; }

        public LocalDateTime getDtCadastro() { return dtCadastro; }
        public void setDtCadastro(LocalDateTime dtCadastro) { this.dtCadastro = dtCadastro; }

        public LocalDateTime getDtManutencao() { return dtManutencao; }
        public void setDtManutencao(LocalDateTime dtManutencao) { this.dtManutencao = dtManutencao; }

        public int getCdUsuario() { return cdUsuario; }
        public void setCdUsuario(int cdUsuario) { this.cdUsuario = cdUsuario; }
    }

    public static class PrestacaoDTO {
        private BigDecimal cdSequencial;
        private int nrAcordo;
        private int cdEmpresaAcordo;
        private int cdFornecedorAcordo;
        private int cdEmpresaDocumento;
        private int nrDocumento;
        private int nrPrestacao;
        private int cdEmpresaNota;
        private int nrNota;
        private String txSerieNota;
        private int cdTransacionadorNota;
        private LocalDate dtNota;
        private BigDecimal vlPrestacao;
        private LocalDate dtVencimento;
        private BigDecimal vlTotalDocumento;
        private String flUtilizado;
        private int cdFormaPagamento;
        private int cdCondicaoPagamento;
        private int cdBanco;
        private int cdOrigem;

        public BigDecimal getCdSequencial() { return cdSequencial; }
        public void setCdSequencial(BigDecimal cdSequencial) { this.cdSequencial = cdSequencial; }

        public int getNrAcordo() { return nrAcordo; }
        public void setNrAcordo(int nrAcordo) { this.nrAcordo = nrAcordo; }

        public int getCdEmpresaAcordo() { return cdEmpresaAcordo; }
        public void setCdEmpresaAcordo(int cdEmpresaAcordo) { this.cdEmpresaAcordo = cdEmpresaAcordo; }

        public int getCdFornecedorAcordo() { return cdFornecedorAcordo; }
        public void setCdFornecedorAcordo(int cdFornecedorAcordo) { this.cdFornecedorAcordo = cdFornecedorAcordo; }

        public int getCdEmpresaDocumento() { return cdEmpresaDocumento; }
        public void setCdEmpresaDocumento(int cdEmpresaDocumento) { this.cdEmpresaDocumento = cdEmpresaDocumento; }

        public int getNrDocumento() { return nrDocumento; }
        public void setNrDocumento(int nrDocumento) { this.nrDocumento = nrDocumento; }

        public int getNrPrestacao() { return nrPrestacao; }
        public void setNrPrestacao(int nrPrestacao) { this.nrPrestacao = nrPrestacao; }

        public int getCdEmpresaNota() { return cdEmpresaNota; }
        public void setCdEmpresaNota(int cdEmpresaNota) { this.cdEmpresaNota = cdEmpresaNota; }

        public int getNrNota() { return nrNota; }
        public void setNrNota(int nrNota) { this.nrNota = nrNota; }

        public String getTxSerieNota() { return txSerieNota; }
        public void setTxSerieNota(String txSerieNota) { this.txSerieNota = txSerieNota; }

        public int getCdTransacionadorNota() { return cdTransacionadorNota; }
        public void setCdTransacionadorNota(int cdTransacionadorNota) { this.cdTransacionadorNota = cdTransacionadorNota; }

        public LocalDate getDtNota() { return dtNota; }
        public void setDtNota(LocalDate dtNota) { this.dtNota = dtNota; }

        public BigDecimal getVlPrestacao() { return vlPrestacao; }
        public void setVlPrestacao(BigDecimal vlPrestacao) { this.vlPrestacao = vlPrestacao; }

        public LocalDate getDtVencimento() { return dtVencimento; }
        public void setDtVencimento(LocalDate dtVencimento) { this.dtVencimento = dtVencimento; }

        public BigDecimal getVlTotalDocumento() { return vlTotalDocumento; }
        public void setVlTotalDocumento(BigDecimal vlTotalDocumento) { this.vlTotalDocumento = vlTotalDocumento; }

        public String getFlUtilizado() { return flUtilizado; }
        public void setFlUtilizado(String flUtilizado) { this.flUtilizado = flUtilizado; }

        public int getCdFormaPagamento() { return cdFormaPagamento; }
        public void setCdFormaPagamento(int cdFormaPagamento) { this.cdFormaPagamento = cdFormaPagamento; }

        public int getCdCondicaoPagamento() { return cdCondicaoPagamento; }
        public void setCdCondicaoPagamento(int cdCondicaoPagamento) { this.cdCondicaoPagamento = cdCondicaoPagamento; }

        public int getCdBanco() { return cdBanco; }
        public void setCdBanco(int cdBanco) { this.cdBanco = cdBanco; }

        public int getCdOrigem() { return cdOrigem; }
        public void setCdOrigem(int cdOrigem) { this.cdOrigem = cdOrigem; }
    }

    public static class DistribuicaoDTO {
        private BigDecimal idDistribuicao;
        private BigDecimal cdAcordo;
        private int cdOrigem;
        private int cdEmpresa;
        private BigDecimal vlDistribuido;
        private BigDecimal pcDistribuido;

        public BigDecimal getIdDistribuicao() { return idDistribuicao; }
        public void setIdDistribuicao(BigDecimal idDistribuicao) { this.idDistribuicao = idDistribuicao; }

        public BigDecimal getCdAcordo() { return cdAcordo; }
        public void setCdAcordo(BigDecimal cdAcordo) { this.cdAcordo = cdAcordo; }

        public int getCdOrigem() { return cdOrigem; }
        public void setCdOrigem(int cdOrigem) { this.cdOrigem = cdOrigem; }

        public int getCdEmpresa() { return cdEmpresa; }
        public void setCdEmpresa(int cdEmpresa) { this.cdEmpresa = cdEmpresa; }

        public BigDecimal getVlDistribuido() { return vlDistribuido; }
        public void setVlDistribuido(BigDecimal vlDistribuido) { this.vlDistribuido = vlDistribuido; }

        public BigDecimal getPcDistribuido() { return pcDistribuido; }
        public void setPcDistribuido(BigDecimal pcDistribuido) { this.pcDistribuido = pcDistribuido; }
    }

    public static class IntegracaoDocumentoDTO {
        private BigDecimal idSequencial;
        private int cdEmpresa;
        private String txTipo;
        private String txSerie;
        private int cdFornecedor;
        private int nrNumero;

        public BigDecimal getIdSequencial() { return idSequencial; }
        public void setIdSequencial(BigDecimal idSequencial) { this.idSequencial = idSequencial; }

        public int getCdEmpresa() { return cdEmpresa; }
        public void setCdEmpresa(int cdEmpresa) { this.cdEmpresa = cdEmpresa; }

        public String getTxTipo() { return txTipo; }
        public void setTxTipo(String txTipo) { this.txTipo = txTipo; }

        public String getTxSerie() { return txSerie; }
        public void setTxSerie(String txSerie) { this.txSerie = txSerie; }

        public int getCdFornecedor() { return cdFornecedor; }
        public void setCdFornecedor(int cdFornecedor) { this.cdFornecedor = cdFornecedor; }

        public int getNrNumero() { return nrNumero; }
        public void setNrNumero(int nrNumero) { this.nrNumero = nrNumero; }
    }
}
