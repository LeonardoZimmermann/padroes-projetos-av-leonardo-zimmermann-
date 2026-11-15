import java.math.BigDecimal;
public final class ComissaoImoveis implements RegraComissao {
    public BigDecimal calcular(BigDecimal valorVenda) {
        BigDecimal valor = Dinheiro.validar(valorVenda);
        return Dinheiro.arredondar(valor.multiply(new BigDecimal("0.03")).add(new BigDecimal("1500.00")));
    }
}
