import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
public final class Dinheiro {
    private Dinheiro() { }
    public static BigDecimal validar(BigDecimal valor) {
        Objects.requireNonNull(valor, "valor");
        if (valor.signum() < 0) throw new IllegalArgumentException("Valor nao pode ser negativo");
        return valor.setScale(2, RoundingMode.UNNECESSARY);
    }
    public static BigDecimal arredondar(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
    public static String formatar(BigDecimal valor) {
        return "R$ " + valor.setScale(2, RoundingMode.UNNECESSARY).toPlainString().replace('.', ',');
    }
}
