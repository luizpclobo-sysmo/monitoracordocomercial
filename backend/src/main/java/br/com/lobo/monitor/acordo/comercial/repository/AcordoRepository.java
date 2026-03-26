package br.com.lobo.monitor.acordo.comercial.repository;

import br.com.lobo.monitor.acordo.comercial.dto.AcordoDetalhadoDTO;
import br.com.lobo.monitor.acordo.comercial.dto.AcordoDetalhadoDTO.*;
import br.com.lobo.monitor.acordo.comercial.dto.ProdutoVerbaDTO;
import br.com.lobo.monitor.acordo.comercial.dto.VendaDTO;
import br.com.lobo.monitor.acordo.comercial.dto.VendaPeriodoDTO;
import br.com.lobo.monitor.acordo.comercial.entity.AcordoComercial;
import br.com.lobo.monitor.acordo.comercial.entity.Empresa;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class AcordoRepository {

    @Inject
    DataSource dataSource;

    // =========================================================================
    // 0. Listar empresas
    // =========================================================================
    public List<Empresa> listarEmpresas() {
        String sql = "SELECT E.COD, T.NOM FROM SPSEMP00 E JOIN TRSTRA01 T ON T.EMP = E.COD AND T.COD < 1000 ORDER BY E.COD";
        List<Empresa> empresas = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                empresas.add(new Empresa(rs.getInt("COD"), rs.getString("NOM")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar empresas: " + e.getMessage(), e);
        }
        return empresas;
    }

    // =========================================================================
    // 1. List all acordos (basic info)
    // =========================================================================
    public List<AcordoComercial> listarAcordosSellOut() {
        String sql = """
            SELECT A.ID_ACORDO,
                   A.NR_ACORDO,
                   A.TX_SITUACAO,
                   A.TX_TIPO,
                   A.TX_SUBTIPO,
                   A.CD_EMPRESA,
                   A.CD_USUARIO,
                   A.DT_ACORDOINICIO,
                   A.DT_ACORDOFIM,
                   A.DT_CADASTRO,
                   A.DT_MANUTENCAO
              FROM TB_ACORDOCOMERCIAL A
             ORDER BY A.NR_ACORDO DESC
            """;

        List<AcordoComercial> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapAcordoComercial(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar acordos: " + e.getMessage(), e);
        }
        return result;
    }

    // =========================================================================
    // 2. Acordo header by ID_ACORDO
    // =========================================================================
    public AcordoComercial buscarAcordoPorId(BigDecimal idAcordo) {
        String sql = """
            SELECT A.ID_ACORDO,
                   A.NR_ACORDO,
                   A.TX_SITUACAO,
                   A.TX_TIPO,
                   A.TX_SUBTIPO,
                   A.CD_EMPRESA,
                   A.CD_USUARIO,
                   A.DT_ACORDOINICIO,
                   A.DT_ACORDOFIM,
                   A.DT_CADASTRO,
                   A.DT_MANUTENCAO
              FROM TB_ACORDOCOMERCIAL A
             WHERE A.ID_ACORDO = ?
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, idAcordo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAcordoComercial(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar acordo: " + e.getMessage(), e);
        }
        return null;
    }

    // =========================================================================
    // 3. Sell Out Total (TB_ACORDOCOMERCIALSELLOUTTOTAL)
    // =========================================================================
    public SellOutTotalDTO buscarSellOutTotal(BigDecimal idAcordo) {
        String sql = """
            SELECT CD_ACORDO,
                   FL_FECHAMENTOMANUAL,
                   CD_FORMAPAGAMENTO,
                   CD_CONDICAOPAGAMENTO,
                   TX_CONTROLE,
                   TX_MENSAGEM,
                   FL_ATRIBUIRVERBACONTACOMERCIAL
              FROM TB_ACORDOCOMERCIALSELLOUTTOTAL
             WHERE CD_ACORDO = ?
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, idAcordo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SellOutTotalDTO dto = new SellOutTotalDTO();
                    dto.setCdAcordo(rs.getBigDecimal("CD_ACORDO"));
                    dto.setFlFechamentoManual(rs.getString("FL_FECHAMENTOMANUAL"));
                    dto.setCdFormaPagamento(rs.getInt("CD_FORMAPAGAMENTO"));
                    dto.setCdCondicaoPagamento(rs.getInt("CD_CONDICAOPAGAMENTO"));
                    dto.setTxControle(rs.getString("TX_CONTROLE"));
                    dto.setTxMensagem(rs.getString("TX_MENSAGEM"));
                    dto.setFlAtribuirVerbaContaComercial(rs.getString("FL_ATRIBUIRVERBACONTACOMERCIAL"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar sell out total: " + e.getMessage(), e);
        }
        return null;
    }

    // =========================================================================
    // 4. Sell Out Items (TB_ACORDOCOMERCIALSELLOUTITEM)
    // =========================================================================
    public List<SellOutItemDTO> buscarItens(BigDecimal idAcordo) {
        String sql = """
            SELECT ITEM.ID_CODIGO,
                   ITEM.CD_ACORDO,
                   ITEM.CD_PRODUTO,
                   PRODUTO.DSC AS TX_PRODUTO,
                   ITEM.PC_UNIDADE,
                   ITEM.VL_UNIDADE
              FROM TB_ACORDOCOMERCIALSELLOUTITEM ITEM
              LEFT JOIN GCEPRO02 PRODUTO ON PRODUTO.COD = ITEM.CD_PRODUTO
             WHERE ITEM.CD_ACORDO = ?
             ORDER BY PRODUTO.DSC, ITEM.CD_PRODUTO
            """;

        List<SellOutItemDTO> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, idAcordo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SellOutItemDTO dto = new SellOutItemDTO();
                    dto.setIdCodigo(rs.getBigDecimal("ID_CODIGO"));
                    dto.setCdAcordo(rs.getBigDecimal("CD_ACORDO"));
                    dto.setCdProduto(rs.getInt("CD_PRODUTO"));
                    dto.setTxProdutoDescricao(rs.getString("TX_PRODUTO"));
                    dto.setPcUnidade(rs.getBigDecimal("PC_UNIDADE"));
                    dto.setVlUnidade(rs.getBigDecimal("VL_UNIDADE"));
                    result.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar itens sell out: " + e.getMessage(), e);
        }
        return result;
    }

    // =========================================================================
    // 5. Empresas (TB_ACORDOCOMERCIALEMPRESA)
    // =========================================================================
    public List<EmpresaDTO> buscarEmpresas(BigDecimal idAcordo) {
        String sql = """
            SELECT CD_ACORDO, CD_EMPRESA
              FROM TB_ACORDOCOMERCIALEMPRESA
             WHERE CD_ACORDO = ?
             ORDER BY CD_EMPRESA
            """;

        List<EmpresaDTO> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, idAcordo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EmpresaDTO dto = new EmpresaDTO();
                    dto.setCdAcordo(rs.getBigDecimal("CD_ACORDO"));
                    dto.setCdEmpresa(rs.getInt("CD_EMPRESA"));
                    result.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar empresas: " + e.getMessage(), e);
        }
        return result;
    }

    // =========================================================================
    // 6. Transacionador (TB_ACORDOCOMERCIALTRANSACIONADOR)
    // =========================================================================
    public TransacionadorDTO buscarTransacionador(BigDecimal idAcordo) {
        String sql = """
            SELECT CD_ACORDO, CD_FORNECEDOR, CD_COMPRADOR
              FROM TB_ACORDOCOMERCIALTRANSACIONADOR
             WHERE CD_ACORDO = ?
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, idAcordo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TransacionadorDTO dto = new TransacionadorDTO();
                    dto.setCdAcordo(rs.getBigDecimal("CD_ACORDO"));
                    dto.setCdFornecedor(rs.getInt("CD_FORNECEDOR"));
                    dto.setCdComprador(rs.getInt("CD_COMPRADOR"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar transacionador: " + e.getMessage(), e);
        }
        return null;
    }

    // =========================================================================
    // 7. Processamento cabecalho (TB_ACORDOCOMERCIALSELLOUTPROCESSAMENTO)
    // =========================================================================
    public List<ProcessamentoDTO> buscarProcessamentos(BigDecimal idAcordo) {
        String sql = """
            SELECT ID_SEQUENCIAL,
                   CD_ACORDO,
                   CD_TIPO,
                   CD_ETAPA,
                   QT_TENTATIVAS,
                   DT_PROCESSAMENTO,
                   TX_OBSERVACAO
              FROM TB_ACORDOCOMERCIALSELLOUTPROCESSAMENTO
             WHERE CD_ACORDO = ?
             ORDER BY DT_PROCESSAMENTO DESC
            """;

        List<ProcessamentoDTO> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, idAcordo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProcessamentoDTO dto = new ProcessamentoDTO();
                    dto.setIdSequencial(rs.getBigDecimal("ID_SEQUENCIAL"));
                    dto.setCdAcordo(rs.getBigDecimal("CD_ACORDO"));
                    dto.setCdTipo(rs.getString("CD_TIPO"));
                    dto.setCdEtapa(rs.getString("CD_ETAPA"));
                    dto.setEtapaDescricao(descricaoEtapa(rs.getString("CD_ETAPA")));
                    dto.setQtTentativas(rs.getInt("QT_TENTATIVAS"));
                    dto.setDtProcessamento(toLocalDateTime(rs.getTimestamp("DT_PROCESSAMENTO")));
                    dto.setTxObservacao(rs.getString("TX_OBSERVACAO"));
                    result.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar processamentos: " + e.getMessage(), e);
        }
        return result;
    }

    // =========================================================================
    // 8. Processamento itens (TB_ACORDOCOMERCIALSELLOUTITEMPROCESSAMENTO)
    // =========================================================================
    public List<ProcessamentoItemDTO> buscarProcessamentoItens(BigDecimal idAcordo) {
        String sql = """
            SELECT ID_SEQUENCIAL,
                   CD_ACORDO,
                   CD_EMPRESA,
                   CD_PRODUTO,
                   CD_TIPO,
                   CD_ETAPA
              FROM TB_ACORDOCOMERCIALSELLOUTITEMPROCESSAMENTO
             WHERE CD_ACORDO = ?
             ORDER BY CD_EMPRESA, CD_PRODUTO
            """;

        List<ProcessamentoItemDTO> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, idAcordo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProcessamentoItemDTO dto = new ProcessamentoItemDTO();
                    dto.setIdSequencial(rs.getBigDecimal("ID_SEQUENCIAL"));
                    dto.setCdAcordo(rs.getBigDecimal("CD_ACORDO"));
                    dto.setCdEmpresa(rs.getInt("CD_EMPRESA"));
                    dto.setCdProduto(rs.getInt("CD_PRODUTO"));
                    dto.setCdTipo(rs.getString("CD_TIPO"));
                    dto.setCdEtapa(rs.getString("CD_ETAPA"));
                    dto.setEtapaDescricao(descricaoEtapa(rs.getString("CD_ETAPA")));
                    result.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar processamento itens: " + e.getMessage(), e);
        }
        return result;
    }

    // =========================================================================
    // 9. Periodos/Fragmentos (TB_ACORDOCOMERCIALSELLOUTPERIODO)
    // =========================================================================
    public List<PeriodoDTO> buscarPeriodos(BigDecimal idAcordo) {
        String sql = """
            SELECT ID_SEQUENCIAL,
                   CD_ACORDO,
                   CD_EMPRESA,
                   CD_PRODUTO,
                   DT_INICIO,
                   DT_FIM
              FROM TB_ACORDOCOMERCIALSELLOUTPERIODO
             WHERE CD_ACORDO = ?
             ORDER BY CD_EMPRESA, CD_PRODUTO, DT_INICIO
            """;

        List<PeriodoDTO> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, idAcordo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PeriodoDTO dto = new PeriodoDTO();
                    dto.setIdSequencial(rs.getBigDecimal("ID_SEQUENCIAL"));
                    dto.setCdAcordo(rs.getBigDecimal("CD_ACORDO"));
                    dto.setCdEmpresa(rs.getInt("CD_EMPRESA"));
                    dto.setCdProduto(rs.getInt("CD_PRODUTO"));
                    dto.setDtInicio(toLocalDate(rs.getDate("DT_INICIO")));
                    dto.setDtFim(toLocalDate(rs.getDate("DT_FIM")));
                    result.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar periodos: " + e.getMessage(), e);
        }
        return result;
    }

    public int limparTudo() {
        String[] sqls = {
            "DELETE FROM TB_ACORDOCOMERCIALSELLOUTITEMPROCESSAMENTO",
            "DELETE FROM TB_ACORDOCOMERCIALSELLOUTITEMPROCESSAMENTOHISTORICO",
            "DELETE FROM TB_ACORDOCOMERCIALSELLOUTPERIODO",
            "DELETE FROM TB_ACORDOCOMERCIALSELLOUTTOTAL",
            "DELETE FROM TB_ACORDOCOMERCIALSELLOUTITEM",
            "DELETE FROM TB_ACORDOCOMERCIALSELLOUTPROCESSAMENTO",
            "DELETE FROM TB_ACORDOCOMERCIALSELLOUT",
            "DELETE FROM TB_ACORDOCOMERCIALEMPRESA",
            "DELETE FROM TB_ACORDOCOMERCIALTRANSACIONADOR",
            "DELETE FROM TB_ACORDOCOMERCIALPRESTACAO",
            "DELETE FROM TB_ACORDOCOMERCIALDISTRIBUICAO",
            "DELETE FROM TB_ACORDOCOMERCIALINTEGRACAODOCUMENTO",
            """
            UPDATE GCEPRO05 SET VL_VERBA = 0, VL_VERBAANTECIPACAO = 0, VL_VERBASELLOUT = 0,
                                VL_VERBASELLIN = 0, VL_VERBACONTACOMERCIAL = 0
             WHERE (EMP, COD) IN (
               SELECT E.CD_EMPRESA, I.CD_PRODUTO
                 FROM TB_ACORDOCOMERCIAL A
                 JOIN TB_ACORDOCOMERCIALEMPRESA E ON E.CD_ACORDO = A.ID_ACORDO
                 JOIN TB_ACORDOCOMERCIALSELLOUTITEM I ON I.CD_ACORDO = A.ID_ACORDO
             )
            """,
            "DELETE FROM TB_ACORDOCOMERCIAL",
            "DELETE FROM GCEITM01 WHERE MOD='E' AND OP1='S' AND NUM >= 900000",
            "DELETE FROM GCENFS01 WHERE MOD='E' AND OP1='S' AND NUM >= 900000"
        };
        int total = 0;
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (String sql : sqls) {
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        total += ps.executeUpdate();
                    }
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Erro ao limpar: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro de conexão: " + e.getMessage(), e);
        }
        return total;
    }

    public void atualizarAcordoPeriodo(BigDecimal idAcordo, LocalDate dtInicio, LocalDate dtFim) {
        String sql = "UPDATE TB_ACORDOCOMERCIAL SET DT_ACORDOINICIO = ?, DT_ACORDOFIM = ? WHERE ID_ACORDO = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dtInicio));
            ps.setDate(2, Date.valueOf(dtFim));
            ps.setBigDecimal(3, idAcordo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar periodo do acordo: " + e.getMessage(), e);
        }
    }

    public void atualizarPeriodo(BigDecimal idSequencial, LocalDate dtInicio, LocalDate dtFim) {
        String sql = "UPDATE TB_ACORDOCOMERCIALSELLOUTPERIODO SET DT_INICIO = ?, DT_FIM = ? WHERE ID_SEQUENCIAL = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dtInicio));
            ps.setDate(2, Date.valueOf(dtFim));
            ps.setBigDecimal(3, idSequencial);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar periodo: " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // 10. Lotes (TB_ACORDOCOMERCIALSELLOUT) - CD_ACORDO stores NR_ACORDO
    // =========================================================================
    public List<LoteDTO> buscarLotes(int nrAcordo) {
        String sql = """
            SELECT CD_LOTE,
                   CD_ACORDO,
                   DT_INICIAL,
                   DT_FINAL,
                   VL_RECEBIDO,
                   CD_EMPRESA,
                   DT_CADASTRO,
                   DT_MANUTENCAO,
                   CD_USUARIO
              FROM TB_ACORDOCOMERCIALSELLOUT
             WHERE CD_ACORDO = ?
             ORDER BY DT_INICIAL DESC
            """;

        List<LoteDTO> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, nrAcordo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoteDTO dto = new LoteDTO();
                    dto.setCdLote(rs.getInt("CD_LOTE"));
                    dto.setCdAcordo(rs.getInt("CD_ACORDO"));
                    dto.setDtInicial(toLocalDate(rs.getDate("DT_INICIAL")));
                    dto.setDtFinal(toLocalDate(rs.getDate("DT_FINAL")));
                    dto.setVlRecebido(rs.getBigDecimal("VL_RECEBIDO"));
                    dto.setCdEmpresa(rs.getInt("CD_EMPRESA"));
                    dto.setDtCadastro(toLocalDateTime(rs.getTimestamp("DT_CADASTRO")));
                    dto.setDtManutencao(toLocalDateTime(rs.getTimestamp("DT_MANUTENCAO")));
                    dto.setCdUsuario(rs.getInt("CD_USUARIO"));
                    result.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar lotes: " + e.getMessage(), e);
        }
        return result;
    }

    // =========================================================================
    // 11. Prestacoes (TB_ACORDOCOMERCIALPRESTACAO) - uses NR_ACORDO
    // =========================================================================
    public List<PrestacaoDTO> buscarPrestacoes(int nrAcordo) {
        String sql = """
            SELECT CD_SEQUENCIAL,
                   NR_ACORDO,
                   CD_EMPRESAACORDO,
                   CD_FORNECEDORACORDO,
                   CD_EMPRESADOCUMENTO,
                   NR_DOCUMENTO,
                   NR_PRESTACAO,
                   CD_EMPRESANOTA,
                   NR_NOTA,
                   TX_SERIENOTA,
                   CD_TRANSACIONADORNOTA,
                   DT_NOTA,
                   VL_PRESTACAO,
                   DT_VENCIMENTO,
                   VL_TOTALDOCUMENTO,
                   FL_UTILIZADO,
                   CD_FORMAPAGAMENTO,
                   CD_CONDICAOPAGAMENTO,
                   CD_ORIGEM
              FROM TB_ACORDOCOMERCIALPRESTACAO
             WHERE NR_ACORDO = ?
             ORDER BY CD_EMPRESAACORDO, CD_ORIGEM, NR_PRESTACAO
            """;

        List<PrestacaoDTO> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, nrAcordo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PrestacaoDTO dto = new PrestacaoDTO();
                    dto.setCdSequencial(rs.getBigDecimal("CD_SEQUENCIAL"));
                    dto.setNrAcordo(rs.getInt("NR_ACORDO"));
                    dto.setCdEmpresaAcordo(rs.getInt("CD_EMPRESAACORDO"));
                    dto.setCdFornecedorAcordo(rs.getInt("CD_FORNECEDORACORDO"));
                    dto.setCdEmpresaDocumento(rs.getInt("CD_EMPRESADOCUMENTO"));
                    dto.setNrDocumento(rs.getInt("NR_DOCUMENTO"));
                    dto.setNrPrestacao(rs.getInt("NR_PRESTACAO"));
                    dto.setCdEmpresaNota(rs.getInt("CD_EMPRESANOTA"));
                    dto.setNrNota(rs.getInt("NR_NOTA"));
                    dto.setTxSerieNota(rs.getString("TX_SERIENOTA"));
                    dto.setCdTransacionadorNota(rs.getInt("CD_TRANSACIONADORNOTA"));
                    dto.setDtNota(toLocalDate(rs.getDate("DT_NOTA")));
                    dto.setVlPrestacao(rs.getBigDecimal("VL_PRESTACAO"));
                    dto.setDtVencimento(toLocalDate(rs.getDate("DT_VENCIMENTO")));
                    dto.setVlTotalDocumento(rs.getBigDecimal("VL_TOTALDOCUMENTO"));
                    dto.setFlUtilizado(rs.getString("FL_UTILIZADO"));
                    dto.setCdFormaPagamento(rs.getInt("CD_FORMAPAGAMENTO"));
                    dto.setCdCondicaoPagamento(rs.getInt("CD_CONDICAOPAGAMENTO"));
                    dto.setCdOrigem(rs.getInt("CD_ORIGEM"));
                    result.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar prestacoes: " + e.getMessage(), e);
        }
        return result;
    }

    // =========================================================================
    // 12. Distribuicao (TB_ACORDOCOMERCIALDISTRIBUICAO)
    // =========================================================================
    public List<DistribuicaoDTO> buscarDistribuicoes(BigDecimal idAcordo) {
        String sql = """
            SELECT ID_DISTRIBUICAO,
                   CD_ACORDO,
                   CD_ORIGEM,
                   CD_EMPRESA,
                   VL_DISTRIBUIDO,
                   PC_DISTRIBUIDO
              FROM TB_ACORDOCOMERCIALDISTRIBUICAO
             WHERE CD_ACORDO = ?
             ORDER BY CD_ORIGEM, CD_EMPRESA
            """;

        List<DistribuicaoDTO> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, idAcordo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DistribuicaoDTO dto = new DistribuicaoDTO();
                    dto.setIdDistribuicao(rs.getBigDecimal("ID_DISTRIBUICAO"));
                    dto.setCdAcordo(rs.getBigDecimal("CD_ACORDO"));
                    dto.setCdOrigem(rs.getInt("CD_ORIGEM"));
                    dto.setCdEmpresa(rs.getInt("CD_EMPRESA"));
                    dto.setVlDistribuido(rs.getBigDecimal("VL_DISTRIBUIDO"));
                    dto.setPcDistribuido(rs.getBigDecimal("PC_DISTRIBUIDO"));
                    result.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar distribuicoes: " + e.getMessage(), e);
        }
        return result;
    }

    // =========================================================================
    // 13. Integracao Documento (TB_ACORDOCOMERCIALINTEGRACAODOCUMENTO)
    // =========================================================================
    public List<IntegracaoDocumentoDTO> buscarIntegracaoDocumentos(BigDecimal idAcordo) {
        String sql = """
            SELECT DOC.ID_SEQUENCIAL,
                   DOC.CD_EMPRESA,
                   DOC.TX_TIPO,
                   DOC.TX_SERIE,
                   DOC.CD_FORNECEDOR,
                   DOC.NR_NUMERO
              FROM TB_ACORDOCOMERCIALINTEGRACAODOCUMENTO DOC
              JOIN TB_ACORDOCOMERCIALINTEGRACAODOCUMENTOHISTORICO HIST
                   ON HIST.CD_INTEGRACAO = DOC.ID_SEQUENCIAL
                  AND HIST.CD_ACORDO = ?
             ORDER BY DOC.CD_EMPRESA, DOC.TX_TIPO
            """;

        List<IntegracaoDocumentoDTO> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, idAcordo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    IntegracaoDocumentoDTO dto = new IntegracaoDocumentoDTO();
                    dto.setIdSequencial(rs.getBigDecimal("ID_SEQUENCIAL"));
                    dto.setCdEmpresa(rs.getInt("CD_EMPRESA"));
                    dto.setTxTipo(rs.getString("TX_TIPO"));
                    dto.setTxSerie(rs.getString("TX_SERIE"));
                    dto.setCdFornecedor(rs.getInt("CD_FORNECEDOR"));
                    dto.setNrNumero(rs.getInt("NR_NUMERO"));
                    result.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar integracao documentos: " + e.getMessage(), e);
        }
        return result;
    }

    // =========================================================================
    // 14. Vendas (GCEITM01 + GCENFS01) - Sales matching acordo period/products
    // =========================================================================
    public List<VendaDTO> buscarVendas(BigDecimal idAcordo) {
        // First get the acordo info we need: period, products, and empresas
        AcordoComercial acordo = buscarAcordoPorId(idAcordo);
        if (acordo == null) return new ArrayList<>();

        List<SellOutItemDTO> itens = buscarItens(idAcordo);
        if (itens.isEmpty()) return new ArrayList<>();

        List<EmpresaDTO> empresas = buscarEmpresas(idAcordo);
        if (empresas.isEmpty()) return new ArrayList<>();

        List<Integer> produtos = itens.stream()
                .map(SellOutItemDTO::getCdProduto)
                .collect(Collectors.toList());

        List<Integer> empresaIds = empresas.stream()
                .map(EmpresaDTO::getCdEmpresa)
                .collect(Collectors.toList());

        String produtosIn = produtos.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String empresasIn = empresaIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        // Query matching the Delphi logic: MOD='E', OP1='S', OP2 in ('V','F')
        String sql = "SELECT GCEITM01.EMP AS CD_EMPRESA,"
                + " GCEITM01.PRO AS CD_PRODUTO,"
                + " GCENFS01.NUM AS NR_NUMERO,"
                + " GCENFS01.SER AS TX_SERIE,"
                + " GCEITM01.DTR AS DT_REGISTRO,"
                + " GCEITM01.QNT AS QUANTIDADE,"
                + " GCEITM01.VTL AS VALOR_TOTAL,"
                + " GCEITM01.OP1 AS OP1,"
                + " GCEITM01.OP2 AS OP2,"
                + " GCEITM01.MOD AS MOD,"
                + " GCENFS01.CFL AS CFL,"
                + " GCENFS01.VTM AS VALOR_MERCADORIA"
                + " FROM GCEITM01"
                + " JOIN GCENFS01 ON GCENFS01.MOD = GCEITM01.MOD"
                + " AND GCENFS01.EMP = GCEITM01.EMP"
                + " AND GCENFS01.DTR = GCEITM01.DTR"
                + " AND GCENFS01.OP1 = GCEITM01.OP1"
                + " AND GCENFS01.NUM = GCEITM01.NUM"
                + " AND GCENFS01.SER = GCEITM01.SER"
                + " AND GCENFS01.CCF = GCEITM01.CCF"
                + " AND GCENFS01.PDV = 0"
                + " AND GCENFS01.CUP = 0"
                + " WHERE GCEITM01.MOD = 'E'"
                + " AND GCEITM01.OP1 = 'S'"
                + " AND GCEITM01.OP2 IN ('V', 'F')"
                + " AND GCEITM01.QNT > 0"
                + " AND GCEITM01.PRO IN (" + produtosIn + ")"
                + " AND GCEITM01.EMP IN (" + empresasIn + ")"
                + " AND GCEITM01.DTR BETWEEN ? AND ?"
                + " AND NOT EXISTS ("
                + " SELECT 1 FROM GCENFR01"
                + " WHERE GCENFR01.MOD = GCENFS01.MOD"
                + " AND GCENFR01.EMP = GCENFS01.EMP"
                + " AND GCENFR01.OP1 = GCENFS01.OP1"
                + " AND GCENFR01.NUM = GCENFS01.NUM"
                + " AND GCENFR01.SER = GCENFS01.SER"
                + " AND GCENFR01.CCF = GCENFS01.CCF"
                + " AND GCENFR01.DTR = GCENFS01.DTR"
                + " AND GCENFR01.NR_MODELO = 65"
                + " )"
                + " ORDER BY GCEITM01.EMP, GCEITM01.PRO, GCEITM01.DTR";

        List<VendaDTO> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(acordo.getDtAcordoInicio()));
            ps.setDate(2, Date.valueOf(acordo.getDtAcordoFim()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VendaDTO dto = new VendaDTO();
                    dto.setCdEmpresa(rs.getInt("CD_EMPRESA"));
                    dto.setCdProduto(rs.getInt("CD_PRODUTO"));
                    dto.setNrNumero(rs.getString("NR_NUMERO"));
                    dto.setTxSerie(rs.getString("TX_SERIE"));
                    dto.setDtRegistro(toLocalDate(rs.getDate("DT_REGISTRO")));
                    dto.setQuantidade(rs.getBigDecimal("QUANTIDADE"));
                    dto.setValorTotal(rs.getBigDecimal("VALOR_TOTAL"));
                    dto.setOp1(rs.getString("OP1"));
                    dto.setOp2(rs.getString("OP2"));
                    dto.setMod(rs.getString("MOD"));
                    dto.setCfl(rs.getInt("CFL"));
                    dto.setValorMercadoria(rs.getBigDecimal("VALOR_MERCADORIA"));
                    result.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar vendas: " + e.getMessage(), e);
        }
        return result;
    }

    // =========================================================================
    // 15. Vendas agrupadas por Periodo/Fragmento (Sell Out)
    // =========================================================================
    public List<VendaPeriodoDTO> buscarVendasPorPeriodo(BigDecimal idAcordo) {
        AcordoComercial acordo = buscarAcordoPorId(idAcordo);
        if (acordo == null) return new ArrayList<>();

        List<SellOutItemDTO> itens = buscarItens(idAcordo);
        if (itens.isEmpty()) return new ArrayList<>();

        List<EmpresaDTO> empresas = buscarEmpresas(idAcordo);
        if (empresas.isEmpty()) return new ArrayList<>();

        // Build maps of product descriptions and cadastro values
        java.util.Map<Integer, String> produtoDesc = new java.util.HashMap<>();
        java.util.Map<Integer, java.math.BigDecimal> produtoPcUnidade = new java.util.HashMap<>();
        java.util.Map<Integer, java.math.BigDecimal> produtoVlUnidade = new java.util.HashMap<>();
        for (SellOutItemDTO item : itens) {
            produtoDesc.put(item.getCdProduto(), item.getTxProdutoDescricao());
            produtoPcUnidade.put(item.getCdProduto(), item.getPcUnidade());
            produtoVlUnidade.put(item.getCdProduto(), item.getVlUnidade());
        }

        // Use periodos if available, otherwise generate from acordo items/empresas/dates
        List<PeriodoDTO> periodos = buscarPeriodos(idAcordo);
        if (periodos.isEmpty()) {
            for (EmpresaDTO emp : empresas) {
                for (SellOutItemDTO item : itens) {
                    PeriodoDTO p = new PeriodoDTO();
                    p.setCdEmpresa(emp.getCdEmpresa());
                    p.setCdProduto(item.getCdProduto());
                    p.setDtInicio(acordo.getDtAcordoInicio());
                    p.setDtFim(acordo.getDtAcordoFim());
                    periodos.add(p);
                }
            }
        }

        String sql = "SELECT COALESCE(SUM(I.QNT), 0) AS QTD_VENDIDA,"
                + " COALESCE(SUM(I.VTL), 0) AS VL_TOTAL,"
                + " COUNT(DISTINCT N.NUM) AS QTD_NOTAS"
                + " FROM GCEITM01 I"
                + " JOIN GCENFS01 N ON N.MOD = I.MOD"
                + " AND N.EMP = I.EMP AND N.DTR = I.DTR"
                + " AND N.OP1 = I.OP1 AND N.NUM = I.NUM"
                + " AND N.SER = I.SER AND N.CCF = I.CCF"
                + " AND N.PDV = 0 AND N.CUP = 0"
                + " WHERE I.MOD = 'E' AND I.OP1 = 'S'"
                + " AND I.OP2 IN ('V', 'F') AND I.QNT > 0"
                + " AND I.EMP = ? AND I.PRO = ?"
                + " AND I.DTR BETWEEN ? AND ?"
                + " AND NOT EXISTS ("
                + " SELECT 1 FROM GCENFR01"
                + " WHERE GCENFR01.MOD = N.MOD AND GCENFR01.EMP = N.EMP"
                + " AND GCENFR01.OP1 = N.OP1 AND GCENFR01.NUM = N.NUM"
                + " AND GCENFR01.SER = N.SER AND GCENFR01.CCF = N.CCF"
                + " AND GCENFR01.DTR = N.DTR AND GCENFR01.NR_MODELO = 65"
                + " )";

        List<VendaPeriodoDTO> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (PeriodoDTO periodo : periodos) {
                ps.setInt(1, periodo.getCdEmpresa());
                ps.setInt(2, periodo.getCdProduto());
                ps.setDate(3, Date.valueOf(periodo.getDtInicio()));
                ps.setDate(4, Date.valueOf(periodo.getDtFim()));

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        VendaPeriodoDTO dto = new VendaPeriodoDTO();
                        dto.setCdEmpresa(periodo.getCdEmpresa());
                        dto.setCdProduto(periodo.getCdProduto());
                        dto.setTxProdutoDescricao(produtoDesc.getOrDefault(periodo.getCdProduto(), null));
                        dto.setPcUnidade(produtoPcUnidade.getOrDefault(periodo.getCdProduto(), null));
                        dto.setVlUnidade(produtoVlUnidade.getOrDefault(periodo.getCdProduto(), null));
                        dto.setDtInicio(periodo.getDtInicio());
                        dto.setDtFim(periodo.getDtFim());
                        dto.setQuantidadeVendida(rs.getBigDecimal("QTD_VENDIDA"));
                        dto.setValorTotal(rs.getBigDecimal("VL_TOTAL"));
                        dto.setQuantidadeNotas(rs.getInt("QTD_NOTAS"));
                        result.add(dto);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar vendas por periodo: " + e.getMessage(), e);
        }
        return result;
    }

    // =========================================================================
    // Full detailed DTO assembly
    // =========================================================================
    public AcordoDetalhadoDTO buscarAcordoDetalhado(BigDecimal idAcordo) {
        AcordoComercial acordo = buscarAcordoPorId(idAcordo);
        if (acordo == null) return null;

        AcordoDetalhadoDTO dto = new AcordoDetalhadoDTO();

        // Header
        dto.setIdAcordo(acordo.getIdAcordo());
        dto.setNrAcordo(acordo.getNrAcordo());
        dto.setTxSituacao(acordo.getTxSituacao());
        dto.setSituacaoDescricao(acordo.getSituacaoDescricao());
        dto.setTxTipo(acordo.getTxTipo());
        dto.setTxSubtipo(acordo.getTxSubtipo());
        dto.setCdEmpresa(acordo.getCdEmpresa());
        dto.setCdUsuario(acordo.getCdUsuario());
        dto.setDtAcordoInicio(acordo.getDtAcordoInicio());
        dto.setDtAcordoFim(acordo.getDtAcordoFim());
        dto.setDtCadastro(acordo.getDtCadastro());
        dto.setDtManutencao(acordo.getDtManutencao());

        // All related data
        dto.setSellOutTotal(buscarSellOutTotal(idAcordo));
        dto.setItens(buscarItens(idAcordo));
        dto.setEmpresas(buscarEmpresas(idAcordo));
        dto.setTransacionador(buscarTransacionador(idAcordo));
        dto.setProcessamentos(buscarProcessamentos(idAcordo));
        dto.setProcessamentoItens(buscarProcessamentoItens(idAcordo));
        dto.setPeriodos(buscarPeriodos(idAcordo));
        dto.setLotes(buscarLotes(acordo.getNrAcordo()));
        dto.setPrestacoes(buscarPrestacoes(acordo.getNrAcordo()));
        dto.setDistribuicoes(buscarDistribuicoes(idAcordo));
        dto.setIntegracaoDocumentos(buscarIntegracaoDocumentos(idAcordo));

        return dto;
    }

    // =========================================================================
    // Helper methods
    // =========================================================================

    // =========================================================================
    // Produtos com Verba (gcepro05 x acordos vigentes)
    // =========================================================================
    public List<ProdutoVerbaDTO> listarProdutosComVerba() {
        String sql = """
            SELECT E.CD_EMPRESA AS EMP,
                   I.CD_PRODUTO AS COD,
                   COALESCE(G.DSC, '') AS TX_DESCRICAO,
                   P.VL_VERBAANTECIPACAO,
                   P.VL_VERBASELLOUT,
                   P.VL_VERBASELLIN,
                   P.VL_VERBACONTACOMERCIAL,
                   A.NR_ACORDO,
                   I.VL_UNIDADE,
                   I.PC_UNIDADE,
                   A.TX_TIPO,
                   A.TX_SUBTIPO,
                   A.DT_ACORDOINICIO,
                   A.DT_ACORDOFIM,
                   A.DT_CADASTRO,
                   CASE WHEN CURRENT_DATE BETWEEN A.DT_ACORDOINICIO AND A.DT_ACORDOFIM
                        THEN TRUE ELSE FALSE END AS VIGENTE,
                   CASE WHEN G.PCB = 'S' THEN PRO4.PP1 ELSE PRO4.PV1 END AS VL_PRECO_VENDA
              FROM TB_ACORDOCOMERCIAL A
              JOIN TB_ACORDOCOMERCIALEMPRESA E ON E.CD_ACORDO = A.ID_ACORDO
              JOIN TB_ACORDOCOMERCIALSELLOUTITEM I ON I.CD_ACORDO = A.ID_ACORDO
         LEFT JOIN GCEPRO02 G ON G.COD = I.CD_PRODUTO
         LEFT JOIN GCEPRO05 P ON P.EMP = E.CD_EMPRESA AND P.COD = I.CD_PRODUTO
         LEFT JOIN GCEPRO04 PRO4 ON PRO4.EMP = E.CD_EMPRESA AND PRO4.COD = I.CD_PRODUTO
             WHERE A.TX_SITUACAO NOT IN ('X', 'E')

         UNION ALL

            SELECT E.CD_EMPRESA AS EMP,
                   I.CD_PRODUTO AS COD,
                   COALESCE(G.DSC, '') AS TX_DESCRICAO,
                   P.VL_VERBAANTECIPACAO,
                   P.VL_VERBASELLOUT,
                   P.VL_VERBASELLIN,
                   P.VL_VERBACONTACOMERCIAL,
                   A.NR_ACORDO,
                   I.VL_UNIDADE,
                   I.PC_UNIDADE,
                   A.TX_TIPO,
                   A.TX_SUBTIPO,
                   A.DT_ACORDOINICIO,
                   A.DT_ACORDOFIM,
                   A.DT_CADASTRO,
                   CASE WHEN CURRENT_DATE BETWEEN A.DT_ACORDOINICIO AND A.DT_ACORDOFIM
                        THEN TRUE ELSE FALSE END AS VIGENTE,
                   CASE WHEN G.PCB = 'S' THEN PRO4.PP1 ELSE PRO4.PV1 END AS VL_PRECO_VENDA
              FROM TB_ACORDOCOMERCIAL A
              JOIN TB_ACORDOCOMERCIALEMPRESA E ON E.CD_ACORDO = A.ID_ACORDO
              JOIN TB_ACORDOCOMERCIALSELLINITEM I ON I.CD_ACORDO = A.ID_ACORDO
         LEFT JOIN GCEPRO02 G ON G.COD = I.CD_PRODUTO
         LEFT JOIN GCEPRO05 P ON P.EMP = E.CD_EMPRESA AND P.COD = I.CD_PRODUTO
         LEFT JOIN GCEPRO04 PRO4 ON PRO4.EMP = E.CD_EMPRESA AND PRO4.COD = I.CD_PRODUTO
             WHERE A.TX_SITUACAO NOT IN ('X', 'E')

             ORDER BY NR_ACORDO, EMP, COD
            """;

        List<ProdutoVerbaDTO> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ProdutoVerbaDTO dto = new ProdutoVerbaDTO();
                dto.setCdEmpresa(rs.getInt("EMP"));
                dto.setCdProduto(rs.getInt("COD"));
                dto.setTxProdutoDescricao(rs.getString("TX_DESCRICAO"));
                dto.setVlVerbaAntecipacao(rs.getBigDecimal("VL_VERBAANTECIPACAO"));
                dto.setVlVerbaSellOut(rs.getBigDecimal("VL_VERBASELLOUT"));
                dto.setVlVerbaSellIn(rs.getBigDecimal("VL_VERBASELLIN"));
                dto.setVlVerbaContaComercial(rs.getBigDecimal("VL_VERBACONTACOMERCIAL"));
                dto.setNrAcordoVigente(rs.getInt("NR_ACORDO"));
                dto.setVlUnidadeAcordo(rs.getBigDecimal("VL_UNIDADE"));
                dto.setPcUnidadeAcordo(rs.getBigDecimal("PC_UNIDADE"));
                dto.setTxTipoAcordo(rs.getString("TX_TIPO"));
                dto.setTxSubtipoAcordo(rs.getString("TX_SUBTIPO"));
                dto.setVigente(rs.getBoolean("VIGENTE"));
                dto.setDtAcordoInicio(toLocalDate(rs.getDate("DT_ACORDOINICIO")));
                dto.setDtAcordoFim(toLocalDate(rs.getDate("DT_ACORDOFIM")));
                dto.setDtCadastro(toLocalDate(rs.getDate("DT_CADASTRO")));
                dto.setVlPrecoVenda(rs.getBigDecimal("VL_PRECO_VENDA"));

                // Validação: verba do gcepro05 deve bater com o valor do acordo por tipo
                String tipo = (dto.getTxTipoAcordo() != null ? dto.getTxTipoAcordo().trim() : "")
                            + (dto.getTxSubtipoAcordo() != null ? dto.getTxSubtipoAcordo().trim() : "");
                BigDecimal vlAcordo = dto.getVlUnidadeAcordo() != null ? dto.getVlUnidadeAcordo() : BigDecimal.ZERO;
                BigDecimal pcAcordo = dto.getPcUnidadeAcordo() != null ? dto.getPcUnidadeAcordo() : BigDecimal.ZERO;
                BigDecimal precoVenda = dto.getVlPrecoVenda() != null ? dto.getVlPrecoVenda() : BigDecimal.ZERO;

                BigDecimal verbaGcepro;
                String nomeVerba;
                switch (tipo) {
                    case "SO":
                        verbaGcepro = dto.getVlVerbaSellOut() != null ? dto.getVlVerbaSellOut() : BigDecimal.ZERO;
                        nomeVerba = "Sell-Out";
                        break;
                    case "SI":
                        verbaGcepro = dto.getVlVerbaSellIn() != null ? dto.getVlVerbaSellIn() : BigDecimal.ZERO;
                        nomeVerba = "Sell-In";
                        break;
                    case "AV":
                        verbaGcepro = dto.getVlVerbaAntecipacao() != null ? dto.getVlVerbaAntecipacao() : BigDecimal.ZERO;
                        nomeVerba = "Antecipacao";
                        break;
                    default:
                        verbaGcepro = BigDecimal.ZERO;
                        nomeVerba = tipo;
                        break;
                }

                if (pcAcordo.compareTo(BigDecimal.ZERO) > 0) {
                    // Acordo por percentual: verba esperada = precoVenda × (percentual / 100)
                    if (precoVenda.compareTo(BigDecimal.ZERO) == 0) {
                        dto.setCorreto(false);
                        dto.setMotivoInconsistencia("Preço de venda zerado ou não cadastrado em GCEPRO04 — não é possível calcular verba esperada");
                    } else {
                        BigDecimal verbaEsperada = precoVenda
                                .multiply(pcAcordo)
                                .divide(BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP)
                                .setScale(2, java.math.RoundingMode.HALF_UP);
                        dto.setVlVerbaEsperada(verbaEsperada);
                        if (verbaGcepro.compareTo(BigDecimal.ZERO) == 0) {
                            dto.setCorreto(false);
                            dto.setMotivoInconsistencia("Produto no acordo mas sem verba " + nomeVerba + " no cadastro");
                        } else if (verbaGcepro.setScale(2, java.math.RoundingMode.HALF_UP).compareTo(verbaEsperada) != 0) {
                            dto.setCorreto(false);
                            dto.setMotivoInconsistencia("Verba " + nomeVerba + " (" + verbaGcepro + ") difere do esperado pelo acordo (" + pcAcordo + "% × " + precoVenda + " = " + verbaEsperada + ")");
                        } else {
                            dto.setCorreto(true);
                        }
                    }
                } else if (vlAcordo.compareTo(BigDecimal.ZERO) > 0) {
                    // Acordo por valor fixo
                    dto.setVlVerbaEsperada(vlAcordo);
                    if (verbaGcepro.compareTo(BigDecimal.ZERO) == 0) {
                        dto.setCorreto(false);
                        dto.setMotivoInconsistencia("Produto no acordo mas sem verba " + nomeVerba + " no cadastro");
                    } else if (verbaGcepro.compareTo(vlAcordo) != 0) {
                        dto.setCorreto(false);
                        dto.setMotivoInconsistencia("Verba " + nomeVerba + " (" + verbaGcepro + ") difere do acordo (" + vlAcordo + ")");
                    } else {
                        dto.setCorreto(true);
                    }
                } else {
                    dto.setCorreto(true);
                }

                result.add(dto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos com verba: " + e.getMessage(), e);
        }

        // Resolver sobreposições: para cada (empresa, produto, tipo), apenas o acordo
        // mais específico (período mais curto) é vigente. Regras do Sysmo:
        // 1. Período mais curto (mais específico) vence
        // 2. Mesmo período: maior % > maior valor > % sobre valor > cadastro mais recente
        Map<String, ProdutoVerbaDTO> vigentePorChave = new HashMap<>();
        for (ProdutoVerbaDTO dto : result) {
            if (!dto.isVigente()) continue;
            String tipo = (dto.getTxTipoAcordo() != null ? dto.getTxTipoAcordo().trim() : "")
                        + (dto.getTxSubtipoAcordo() != null ? dto.getTxSubtipoAcordo().trim() : "");
            String chave = dto.getCdEmpresa() + "|" + dto.getCdProduto() + "|" + tipo;
            ProdutoVerbaDTO atual = vigentePorChave.get(chave);
            if (atual == null) {
                vigentePorChave.put(chave, dto);
            } else {
                // Comparar: o mais específico vence
                ProdutoVerbaDTO vencedor = resolverSobreposicao(atual, dto);
                vigentePorChave.put(chave, vencedor);
            }
        }
        // Marcar como não vigente os que perderam a disputa
        for (ProdutoVerbaDTO dto : result) {
            if (!dto.isVigente()) continue;
            String tipo = (dto.getTxTipoAcordo() != null ? dto.getTxTipoAcordo().trim() : "")
                        + (dto.getTxSubtipoAcordo() != null ? dto.getTxSubtipoAcordo().trim() : "");
            String chave = dto.getCdEmpresa() + "|" + dto.getCdProduto() + "|" + tipo;
            if (vigentePorChave.get(chave) != dto) {
                dto.setVigente(false);
            }
        }

        return result;
    }

    /**
     * Resolve sobreposição de acordos vigentes conforme regras do Sysmo (ValidarAcordoMaisFavoravel):
     * 1. Período mais curto (mais específico) vence
     * 2. Mesmo período: maior percentual vence
     * 3. Mesmo período: maior valor vence
     * 4. Mesmo período: percentual sobre valor
     * 5. Mesmo período e mesma modalidade: cadastro mais recente vence
     */
    private ProdutoVerbaDTO resolverSobreposicao(ProdutoVerbaDTO a, ProdutoVerbaDTO b) {
        long duracaoA = (a.getDtAcordoFim() != null && a.getDtAcordoInicio() != null)
                ? java.time.temporal.ChronoUnit.DAYS.between(a.getDtAcordoInicio(), a.getDtAcordoFim()) : Long.MAX_VALUE;
        long duracaoB = (b.getDtAcordoFim() != null && b.getDtAcordoInicio() != null)
                ? java.time.temporal.ChronoUnit.DAYS.between(b.getDtAcordoInicio(), b.getDtAcordoFim()) : Long.MAX_VALUE;

        // Regra 1: período mais curto vence
        if (duracaoA != duracaoB) {
            return duracaoA < duracaoB ? a : b;
        }

        // Mesmo período - sub-regras
        BigDecimal pcA = a.getPcUnidadeAcordo() != null ? a.getPcUnidadeAcordo() : BigDecimal.ZERO;
        BigDecimal pcB = b.getPcUnidadeAcordo() != null ? b.getPcUnidadeAcordo() : BigDecimal.ZERO;
        BigDecimal vlA = a.getVlUnidadeAcordo() != null ? a.getVlUnidadeAcordo() : BigDecimal.ZERO;
        BigDecimal vlB = b.getVlUnidadeAcordo() != null ? b.getVlUnidadeAcordo() : BigDecimal.ZERO;
        boolean aTemPc = pcA.compareTo(BigDecimal.ZERO) > 0;
        boolean bTemPc = pcB.compareTo(BigDecimal.ZERO) > 0;

        // Regra 2: ambos percentual -> maior vence
        if (aTemPc && bTemPc) {
            return pcA.compareTo(pcB) >= 0 ? a : b;
        }

        // Regra 3: ambos valor -> maior vence
        if (!aTemPc && !bTemPc) {
            int cmpVl = vlA.compareTo(vlB);
            if (cmpVl != 0) return cmpVl > 0 ? a : b;
        }

        // Regra 4: percentual vence sobre valor
        if (aTemPc && !bTemPc) return a;
        if (bTemPc && !aTemPc) return b;

        // Regra 5: cadastro mais recente vence
        if (a.getDtCadastro() != null && b.getDtCadastro() != null) {
            return a.getDtCadastro().isAfter(b.getDtCadastro()) ? a : b;
        }
        return a;
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private AcordoComercial mapAcordoComercial(ResultSet rs) throws SQLException {
        AcordoComercial a = new AcordoComercial();
        a.setIdAcordo(rs.getBigDecimal("ID_ACORDO"));
        a.setNrAcordo(rs.getInt("NR_ACORDO"));
        a.setTxSituacao(rs.getString("TX_SITUACAO"));
        a.setTxTipo(rs.getString("TX_TIPO"));
        a.setTxSubtipo(rs.getString("TX_SUBTIPO"));
        a.setCdEmpresa(rs.getInt("CD_EMPRESA"));
        a.setCdUsuario(rs.getInt("CD_USUARIO"));
        a.setDtAcordoInicio(toLocalDate(rs.getDate("DT_ACORDOINICIO")));
        a.setDtAcordoFim(toLocalDate(rs.getDate("DT_ACORDOFIM")));
        a.setDtCadastro(toLocalDateTime(rs.getTimestamp("DT_CADASTRO")));
        a.setDtManutencao(toLocalDateTime(rs.getTimestamp("DT_MANUTENCAO")));
        return a;
    }

    private LocalDate toLocalDate(Date date) {
        return date != null ? date.toLocalDate() : null;
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }

    private String descricaoEtapa(String etapa) {
        if (etapa == null) return "Desconhecida";
        return switch (etapa) {
            case "A" -> "Aguardando";
            case "AP" -> "Aguardando Produtos";
            case "PP" -> "Processando Produtos";
            case "AF" -> "Aguardando Fechamento";
            case "PF" -> "Processando Fechamento";
            case "PV" -> "Processando Verba";
            case "AV" -> "Aguardando Verba";
            case "X" -> "Finalizado";
            case "E" -> "Erro";
            default -> etapa;
        };
    }
}
