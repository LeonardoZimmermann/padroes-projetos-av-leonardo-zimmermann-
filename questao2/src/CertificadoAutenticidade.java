import java.math.BigDecimal;
public final class CertificadoAutenticidade extends ServicoAdicional {
    public CertificadoAutenticidade(Fatura fatura) { super(fatura); }
    protected ItemFatura criarItem() { return new ItemFatura("Certificado de autenticidade", new BigDecimal("300.00")); }
}
