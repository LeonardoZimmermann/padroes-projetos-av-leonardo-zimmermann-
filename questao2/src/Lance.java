import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
public final class Lance {
    private final Licitante participante;
    private final BigDecimal valor;
    private final Instant dataHora;
    public Lance(Licitante participante, BigDecimal valor) {
        this.participante = Objects.requireNonNull(participante, "participante");
        this.valor = Dinheiro.validar(valor);
        this.dataHora = Instant.now();
    }
    public Licitante getParticipante() { return participante; }
    public BigDecimal getValor() { return valor; }
    public Instant getDataHora() { return dataHora; }
}
