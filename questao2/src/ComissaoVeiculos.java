import java.math.BigDecimal;
public final class ComissaoVeiculos implements RegraComissao {
    public BigDecimal calcular(BigDecimal valorVenda) {
        BigDecimal valor = Dinheiro.validar(valorVenda);
        return Dinheiro.arredondar(valor.multiply(new BigDecimal("0.08")).min(new BigDecimal("5000.00")));
    }
}
