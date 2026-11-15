import java.util.Objects;
public final class Solicitacao {
    private final String nome;
    private final String provedor;
    private final int quantidadeMaquinas;
    private final long gb;
    public Solicitacao(String nome, String provedor, int quantidadeMaquinas, long gb) {
        this.nome = Objects.requireNonNull(nome, "nome").trim();
        this.provedor = Objects.requireNonNull(provedor, "provedor").trim();
        if (this.nome.isEmpty() || this.provedor.isEmpty() || quantidadeMaquinas <= 0 || gb <= 0)
            throw new IllegalArgumentException("Nome/provedor obrigatorios; quantidade de VMs e GB devem ser positivos");
        this.quantidadeMaquinas = quantidadeMaquinas;
        this.gb = gb;
    }
    public String getNome() { return nome; }
    public String getProvedor() { return provedor; }
    public int getQuantidadeMaquinas() { return quantidadeMaquinas; }
    public long getGb() { return gb; }
}
