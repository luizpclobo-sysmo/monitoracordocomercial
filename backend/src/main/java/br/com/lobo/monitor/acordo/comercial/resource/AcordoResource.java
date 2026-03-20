package br.com.lobo.monitor.acordo.comercial.resource;

import br.com.lobo.monitor.acordo.comercial.dto.AcordoDetalhadoDTO;
import br.com.lobo.monitor.acordo.comercial.dto.ProdutoVerbaDTO;
import br.com.lobo.monitor.acordo.comercial.dto.VendaDTO;
import br.com.lobo.monitor.acordo.comercial.dto.VendaPeriodoDTO;
import br.com.lobo.monitor.acordo.comercial.entity.AcordoComercial;
import br.com.lobo.monitor.acordo.comercial.entity.Empresa;
import br.com.lobo.monitor.acordo.comercial.repository.AcordoRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Path("/api/acordos")
@Produces(MediaType.APPLICATION_JSON)
public class AcordoResource {

    @Inject
    AcordoRepository repository;

    @GET
    @Path("/empresas")
    public List<Empresa> listarEmpresas() {
        return repository.listarEmpresas();
    }

    /**
     * GET /api/acordos
     * List all Sell Out acordos (TX_TIPO='S', TX_SUBTIPO='O') with basic info.
     */
    @GET
    public List<AcordoComercial> listarAcordosSellOut() {
        return repository.listarAcordosSellOut();
    }

    /**
     * GET /api/acordos/{id}
     * Full detailed view of one acordo, consolidating ALL related data.
     */
    @GET
    @Path("/{id}")
    public Response buscarAcordoDetalhado(@PathParam("id") BigDecimal id) {
        AcordoDetalhadoDTO dto = repository.buscarAcordoDetalhado(id);
        if (dto == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"erro\": \"Acordo nao encontrado\"}")
                    .build();
        }
        return Response.ok(dto).build();
    }

    /**
     * GET /api/acordos/{id}/vendas
     * Sales data (GCEITM01 + GCENFS01) for the acordo's period and products.
     * Filters: MOD='E', OP1='S', OP2 in ('V','F'), QNT > 0
     */
    @GET
    @Path("/{id}/vendas")
    public Response buscarVendas(@PathParam("id") BigDecimal id) {
        AcordoComercial acordo = repository.buscarAcordoPorId(id);
        if (acordo == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"erro\": \"Acordo nao encontrado\"}")
                    .build();
        }

        List<VendaDTO> vendas = repository.buscarVendas(id);
        return Response.ok(vendas).build();
    }

    /**
     * GET /api/acordos/{id}/vendas-periodo
     * Sales aggregated by periodo/fragmento (Sell Out).
     */
    @GET
    @Path("/{id}/vendas-periodo")
    public Response buscarVendasPorPeriodo(@PathParam("id") BigDecimal id) {
        AcordoComercial acordo = repository.buscarAcordoPorId(id);
        if (acordo == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"erro\": \"Acordo nao encontrado\"}")
                    .build();
        }

        List<VendaPeriodoDTO> vendas = repository.buscarVendasPorPeriodo(id);
        return Response.ok(vendas).build();
    }

    /**
     * GET /api/acordos/produtos-verba
     * Products with verba from gcepro05 crossed with vigent acordos.
     */
    @GET
    @Path("/produtos-verba")
    public List<ProdutoVerbaDTO> listarProdutosComVerba() {
        return repository.listarProdutosComVerba();
    }

    /**
     * DELETE /api/acordos/limpar-tudo
     * Deletes all acordos and simulated sales.
     */
    @DELETE
    @Path("/limpar-tudo")
    public Response limparTudo() {
        try {
            int total = repository.limparTudo();
            return Response.ok("{\"sucesso\": true, \"mensagem\": \"Tudo limpo! " + total + " registros removidos.\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"erro\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * PUT /api/acordos/{id}/periodo
     * Update acordo start/end dates.
     */
    @PUT
    @Path("/{id}/periodo")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response atualizarAcordoPeriodo(@PathParam("id") BigDecimal id, Map<String, String> body) {
        try {
            LocalDate dtInicio = LocalDate.parse(body.get("dtAcordoInicio"));
            LocalDate dtFim = LocalDate.parse(body.get("dtAcordoFim"));
            repository.atualizarAcordoPeriodo(id, dtInicio, dtFim);
            return Response.ok("{\"sucesso\": true}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erro\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * PUT /api/acordos/periodos/{idSequencial}
     * Update periodo dates.
     */
    @PUT
    @Path("/periodos/{idSequencial}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response atualizarPeriodo(@PathParam("idSequencial") BigDecimal idSequencial, Map<String, String> body) {
        try {
            LocalDate dtInicio = LocalDate.parse(body.get("dtInicio"));
            LocalDate dtFim = LocalDate.parse(body.get("dtFim"));
            repository.atualizarPeriodo(idSequencial, dtInicio, dtFim);
            return Response.ok("{\"sucesso\": true}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erro\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
