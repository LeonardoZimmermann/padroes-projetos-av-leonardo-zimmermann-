import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
public final class Provisionador {
    private static final BigDecimal HORAS_MES = new BigDecimal("730");
    public BigDecimal estimarCusto(Solicitacao s, ProvedorFactory fabrica) {
        return fabrica.getCustoHoraMaquina().multiply(BigDecimal.valueOf(s.getQuantidadeMaquinas()))
                .multiply(HORAS_MES)
                .add(fabrica.getCustoPorGB().multiply(BigDecimal.valueOf(s.getGb())))
                .add(fabrica.getCustoMensalFila());
    }
    public Resultado provisionar(Solicitacao s, ProvedorFactory fabrica) {
        Objects.requireNonNull(s, "solicitacao");
        Objects.requireNonNull(fabrica, "fabrica");
        Configuracao config = Configuracao.getInstancia();
        List<Recurso> recursos = new ArrayList<>();
        BigDecimal custo = BigDecimal.ZERO;
        try {
            if (!s.getProvedor().equals(fabrica.getNome()))
                return finalizar(s, custo, false, "Provedor solicitado difere da fabrica", recursos);
            // Nenhum recurso ou ID e criado para obter a estimativa.
            custo = estimarCusto(s, fabrica);
            if (custo.compareTo(config.getOrcamentoMensalMaximo()) > 0)
                return finalizar(s, custo, false, "Orcamento excedido: limite R$ "
                        + config.getOrcamentoMensalMaximo().toPlainString(), recursos);
            for (int i = 0; i < s.getQuantidadeMaquinas(); i++) recursos.add(fabrica.criarMaquinaVirtual());
            recursos.add(fabrica.criarArmazenamento());
            recursos.add(fabrica.criarFilaMensagens());
            return finalizar(s, custo, true, "Provisionamento concluido", recursos);
        } catch (RuntimeException e) {
            // Uma falha nunca publica um ambiente com recursos de familias diferentes.
            return finalizar(s, custo, false, "Falha no provisionamento: " + e.getMessage(), new ArrayList<Recurso>());
        }
    }
    private Resultado finalizar(Solicitacao s, BigDecimal custo, boolean aceito,
                                String motivo, List<Recurso> recursos) {
        Resultado resultado = new Resultado(s, Configuracao.getInstancia().getRegiaoPadrao(),
                custo, aceito, motivo, recursos);
        Auditoria.getInstancia().registrar(s, aceito, motivo);
        if (aceito) System.out.println(resultado);
        return resultado;
    }
}
