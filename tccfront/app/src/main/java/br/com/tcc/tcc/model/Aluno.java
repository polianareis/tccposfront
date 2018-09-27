package br.com.tcc.tcc.model;

/**
 * Classe de dominio que representa o aluno
 */

public class Aluno {

    private String email;
    private String senha;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
