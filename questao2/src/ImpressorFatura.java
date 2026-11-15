public final class ImpressorFatura {
    public String gerar(Fatura fatura) {
        Venda venda = fatura.getVenda();
        StringBuilder texto = new StringBuilder("=== FATURA DO COMPRADOR ===\n");
        texto.append("Lote: ").append(venda.getNomeLote()).append("\nComprador: ")
                .append(venda.getComprador().getNome()).append("\nValor de venda: ")
                .append(Dinheiro.formatar(venda.getValor())).append('\n');
        for (ItemFatura item : fatura.getItens())
            texto.append(item.getDescricao()).append(": ").append(Dinheiro.formatar(item.getCusto())).append('\n');
        return texto.append("Total a pagar: ").append(Dinheiro.formatar(fatura.getTotal())).toString();
    }
    public void imprimir(Fatura fatura) { System.out.println(gerar(fatura)); }
}
