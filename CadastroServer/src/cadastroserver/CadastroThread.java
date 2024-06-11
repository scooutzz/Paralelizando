package cadastroserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import model.Produto;
import model.Usuario;

public class CadastroThread extends Thread {
    private Socket s1;
    private ProdutoJpaController ctrl;
    private UsuarioJpaController ctrlUsu;

    public CadastroThread(Socket s1, ProdutoJpaController ctrl, UsuarioJpaController ctrlUsu) {
        this.s1 = s1;
        this.ctrl = ctrl;
        this.ctrlUsu = ctrlUsu;
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
                String command = (String) in.readObject();
                if (command.equals("L")) {
                    List<Produto> produtos = ctrl.findProdutoEntities();
                    out.writeObject(produtos);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}