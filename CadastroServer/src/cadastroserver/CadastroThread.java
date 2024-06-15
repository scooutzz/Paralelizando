package cadastroserver;

import java.math.BigDecimal;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import controller.MovimentoJpaController;
import model.Pessoa;
import model.Produto;
import model.Usuario;
import model.Movimento;

public class CadastroThread extends Thread {
    private final Socket s1;
    private final UsuarioJpaController ctrlUsu;
    private final ProdutoJpaController ctrlProd;
    private final MovimentoJpaController ctrlMov;

    public CadastroThread(Socket s1, UsuarioJpaController ctrlUsu, ProdutoJpaController ctrlProd, MovimentoJpaController ctrlMov) {
        this.s1 = s1;
        this.ctrlUsu = ctrlUsu;
        this.ctrlProd = ctrlProd;
        this.ctrlMov = ctrlMov;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(s1.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(s1.getOutputStream())) {

            String login = (String) in.readObject();
            String senha = (String) in.readObject();

            Usuario usuario = ctrlUsu.findUsuario(login, senha);
            if (usuario == null) {
                out.writeObject("Usuário não encontrado");
                return;
            }

            out.writeObject("Logado com sucesso!");

            while (true) {
                Character command = (Character) in.readObject();
                if (command.equals("L")) {
                    List<Produto> produtos = ctrlProd.findProdutoEntities();
                    out.writeObject(produtos);
                } else if (command.equals("E") || command.equals("S")) {
                    handleMovimento(command, in, out, usuario);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void handleMovimento(Character command, ObjectInputStream in, ObjectOutputStream out, Usuario usuario) throws IOException, ClassNotFoundException {
        try {
            int pessoaId = in.readInt();
            int produtoId = in.readInt();
            int quantidade = in.readInt();
            BigDecimal valorUnitario = (BigDecimal) in.readObject();
            
            Produto produto = ctrlProd.findProduto(produtoId);
            if (produto == null) {
                out.writeObject("Produto não encontrado");
                return;
            }
            
            Pessoa pessoa = new Pessoa();
            pessoa.setPessoaID(pessoaId);
            
            Movimento movimento = new Movimento();
            movimento.setUsuarioID(usuario.getUsuarioID());
            movimento.setTipo(command);
            movimento.setPessoaID(pessoa);
            movimento.setProdutoID(produto);
            movimento.setQuantidade(quantidade);
            movimento.setPrecoUnitario(valorUnitario);
            
            ctrlMov.create(movimento);
            
            if (command.equals("E")) {
                produto.setQuantidade(produto.getQuantidade() + quantidade);
            } else if (command.equals("S")) {
                produto.setQuantidade(produto.getQuantidade() - quantidade);
            }
            ctrlProd.edit(produto);
            
            out.writeObject("Movimento registrado com sucesso");
        } catch (Exception e) {
            e.printStackTrace();
            out.writeObject("Erro ao registrar movimento");
        }
    }
}