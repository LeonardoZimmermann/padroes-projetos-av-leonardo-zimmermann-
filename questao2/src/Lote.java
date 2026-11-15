import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class Lote implements ObservavelLote {
    private final String nome;
    private final String categoria;
    private final BigDecimal precoInicial;
    private final BigDecimal precoReserva;
    private final List<ObservadorLote> observadores = new ArrayList<>();
    private final List<Lance> lances = new ArrayList<>();
    private RegraComissao regraComissao;
    private EstadoLote estado = EstadoLote.ABERTO;
    private Venda venda;
    public Lote(String nome, String categoria, BigDecimal precoInicial,
                BigDecimal precoReserva, RegraComissao regraComissao) {
        this.nome = Objects.requireNonNull(nome, "nome").trim();
        this.categoria = Objects.requireNonNull(categoria, "categoria").trim();
        if (this.nome.isEmpty() || this.categoria.isEmpty())
            throw new IllegalArgumentException("Nome e categoria obrigatorios");
        this.precoInicial = Dinheiro.validar(precoInicial);
        this.precoReserva = Dinheiro.validar(precoReserva);
        this.regraComissao = Objects.requireNonNull(regraComissao, "regraComissao");
    }
    public void acompanhar(ObservadorLote observador) {
        Objects.requireNonNull(observador, "observador");
        if (!observadores.contains(observador)) observadores.add(observador);
    }
    public void deixarDeAcompanhar(ObservadorLote observador) {
        observadores.remove(observador);
    }
    public boolean receberLance(Licitante participante, BigDecimal valor) {
        Objects.requireNonNull(participante, "participante");
        BigDecimal valorLance = Dinheiro.validar(valor);
        if (estado != EstadoLote.ABERTO) {
            System.out.println("Lance RECUSADO em " + nome + ": lote encerrado");
            return false;
        }
        // Arredondar para cima garante aumento de pelo menos 5%, mesmo em centavos.
        BigDecimal minimo = getLanceAtual().multiply(new BigDecimal("1.05"))
                .setScale(2, RoundingMode.CEILING);
        if (valorLance.compareTo(minimo) < 0) {
            System.out.println("Lance RECUSADO em " + nome + ": minimo " + Dinheiro.formatar(minimo)
                    + " (aumento de pelo menos 5%)");
            return false;
        }
        Lance lance = new Lance(participante, valorLance);
        lances.add(lance);
        System.out.println("Lance ACEITO em " + nome + ": " + participante.getNome()
                + " ofereceu " + Dinheiro.formatar(valorLance));
        // A copia permite que um observador deixe de acompanhar durante a notificacao.
        for (ObservadorLote observador : new ArrayList<>(observadores)) {
            if (observador != participante) observador.atualizar(this, lance);
        }
        return true;
    }
    public void trocarRegraComissao(RegraComissao novaRegra) {
        if (estado != EstadoLote.ABERTO) throw new IllegalStateException("Lote encerrado");
        regraComissao = Objects.requireNonNull(novaRegra, "novaRegra");
    }
    public Optional<Venda> encerrar() {
        if (estado != EstadoLote.ABERTO) throw new IllegalStateException("Lote ja encerrado");
        if (!lances.isEmpty() && getLanceAtual().compareTo(precoReserva) >= 0) {
            Lance vencedor = lances.get(lances.size() - 1);
            // Captura a comissao vigente: campanhas futuras nao alteram a venda fechada.
            BigDecimal comissao = regraComissao.calcular(vencedor.getValor());
            venda = new Venda(nome, vencedor.getParticipante(), vencedor.getValor(), comissao);
            estado = EstadoLote.VENDIDO;
            System.out.println("Lote VENDIDO: " + nome + " para " + vencedor.getParticipante().getNome());
        } else {
            estado = EstadoLote.NAO_VENDIDO;
            System.out.println("Lote NAO VENDIDO: " + nome + ": sem lances ou reserva nao atingida");
        }
        return Optional.ofNullable(venda);
    }
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public BigDecimal getPrecoInicial() { return precoInicial; }
    public BigDecimal getPrecoReserva() { return precoReserva; }
    public BigDecimal getLanceAtual() { return lances.isEmpty() ? precoInicial : lances.get(lances.size() - 1).getValor(); }
    public List<Lance> getLances() { return Collections.unmodifiableList(new ArrayList<>(lances)); }
    public EstadoLote getEstado() { return estado; }
}
