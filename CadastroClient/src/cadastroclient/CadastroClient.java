package cadastroclient;

import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.util.List;
import java.util.Scanner;
import model.Produto;

public class CadastroClient {
    public static void main(String[] args) {
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try (Socket socket = new Socket("localhost", 1234)) {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeObject("admin");
            out.flush();
            out.writeObject("123qwe");
            out.flush();

            String loginResponse = (String) in.readObject();
            System.out.println(loginResponse);
            if (!loginResponse.equals("Usuário não encontrado")) {
                Scanner scanner = new Scanner(System.in);
                
                while (true) {
                    System.out.println("Menu:");
                    System.out.println("L - Listar");
                    System.out.println("E - Entrada");
                    System.out.println("S - Saída");
                    System.out.println("X - Finalizar");
                    System.out.println("Escolha uma opcão:");
                    String command = scanner.nextLine().toUpperCase();
                    
                    if (command.equals("X")) {
                        break;
                    }
                    
                    out.writeObject(command);
                    out.flush();
                    
                    switch (command) {
                        case "L":
                            // Listar
                            List<Produto> produtos = (List<Produto>) in.readObject();
                            for (Produto produto : produtos) {
                                System.out.println(produto.getNome());
                            }
                            break;
                        case "E":
                        case "S":
                            System.out.print("Id da pessoa: ");
                            int pessoaId = Integer.parseInt(scanner.nextLine());
                            System.out.print("Id do produto: ");
                            int produtoId = Integer.parseInt(scanner.nextLine());
                            System.out.print("Quantidade: ");
                            int quantidade = Integer.parseInt(scanner.nextLine());
                            System.out.print("Valor unitário: ");
                            BigDecimal valorUnitario = new BigDecimal(scanner.nextLine());

                            out.writeInt(pessoaId);
                            out.writeInt(produtoId);
                            out.writeInt(quantidade);
                            out.writeObject(valorUnitario);
                            out.flush();

                            // Responsta do servidor
                            String movimentoResponse = (String) in.readObject();
                            System.out.println(movimentoResponse);
                            break;
                        default:
                            System.out.println("Comando inválido!");
                    }
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