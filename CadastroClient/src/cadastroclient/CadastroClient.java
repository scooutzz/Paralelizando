package cadastroclient;

import java.io.*;
import static java.lang.System.out;
import java.net.*;
import java.util.List;
import model.Produto;

public class CadastroClient {
    public static void main(String[] args) {
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try (Socket socket = new Socket("localhost", 1234)) {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Enviar login e senha para o servidor
            out.writeObject("admin"); // Substituir "admin" pelo login real
            out.flush();
            out.writeObject("123qwe"); // Substituir "123qwe" pela senha real
            out.flush();

            // Receber resposta de login do servidor
            String loginResponse = (String) in.readObject();
            if (!loginResponse.equals("Usuário não encontrado")) {
                // Enviar comando 'L' para solicitar a lista de produtos
                out.writeObject("L");
                out.flush();

                // Receber lista de produtos do servidor
                List<Produto> produtos = (List<Produto>) in.readObject();
                for (Produto produto : produtos) {
                    System.out.println(produto.getNome());
                }
            } else {
                System.out.println(loginResponse);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}