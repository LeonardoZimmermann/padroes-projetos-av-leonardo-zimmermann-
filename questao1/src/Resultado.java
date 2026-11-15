import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public final class Resultado {
    private final String ambiente;
    private final String provedor;
    private final String regiao;
    private final BigDecimal custo;
    private final boolean aceito;
    private final String motivo;
    private final List<Recurso> recursos;
    Resultado(Solicitacao solicitacao, String regiao, BigDecimal custo, boolean aceito,
              String motivo, List<Recurso> recursos) {
        for (Recurso recurso : recursos) {
            if (!solicitacao.getProvedor().equals(recurso.getProvedor()))
                throw new IllegalArgumentException("Ambiente nao pode combinar provedores");
        }
        this.ambiente = solicitacao.getNome();
        this.provedor = solicitacao.getProvedor();
        this.regiao = regiao;
        this.custo = custo;
        this.aceito = aceito;
        this.motivo = motivo;
        this.recursos = Collections.unmodifiableList(new ArrayList<>(recursos));
    }
    public boolean isAceito() { return aceito; }
    public BigDecimal getCusto() { return custo; }
    public List<Recurso> getRecursos() { return recursos; }
    public String getRegiao() { return regiao; }
    public String getMotivo() { return motivo; }
    public String toString() {
        StringBuilder ids = new StringBuilder();
        for (Recurso recurso : recursos) {
            if (ids.length() > 0) ids.append(", ");
            ids.append(recurso.getIdentificador());
        }
        return ambiente + " | " + provedor + " | regiao=" + regiao + " | "
                + (aceito ? "ACEITO" : "RECUSADO") + " | custo mensal=R$ " + custo.toPlainString()
                + " | recursos=[" + ids + "] | " + motivo;
    }
}
