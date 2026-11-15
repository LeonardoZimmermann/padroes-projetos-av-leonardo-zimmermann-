import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
public final class Participante implements Licitante {
    private final String nome;
    private final List<String> notificacoes = new ArrayList<>();
    public Participante(String nome) {
        this.nome = Objects.requireNonNull(nome, "nome").trim();
        if (this.nome.isEmpty()) throw new IllegalArgumentException("Nome obrigatorio");
    }
    public String getNome() { return nome; }
    public boolean darLance(Lote lote, BigDecimal valor) { return lote.receberLance(this, valor); }
    public void atualizar(Lote lote, Lance lance) {
        String mensagem = nome + " acompanha " + lote.getNome() + ": novo lance " + Dinheiro.formatar(lance.getValor());
        notificacoes.add(mensagem);
        System.out.println(mensagem);
    }
    public List<String> getNotificacoes() { return Collections.unmodifiableList(new ArrayList<>(notificacoes)); }
}
