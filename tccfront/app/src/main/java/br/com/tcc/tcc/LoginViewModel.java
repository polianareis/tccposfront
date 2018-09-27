package br.com.tcc.tcc;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.view.View;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import br.com.tcc.tcc.model.Aluno;

/**
 * Classe responsável por executar toda regra de negócio
 */
public class LoginViewModel {

    private Aluno aluno = new Aluno();

    public boolean login(){
        String url = "https://tccpucserver.herokuapp.com"; //endereço onde está executando o servidor da aplicação
        HttpAuthentication authHeader = new HttpBasicAuthentication(aluno.getEmail(), aluno.getSenha()); // é criado uma autenticação básica utilizando o e-mail e senha
        HttpHeaders requestHeaders = new HttpHeaders(); // é criado o cabeçalho da mensagem
        requestHeaders.setAuthorization(authHeader); //é atribuido autorização na mensagem
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON)); //é solicitado que seja retornado um Json

        //  Cria uma nova instância para consumir o Webservice Rest
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        HttpEntity retorno = null;
        try {
            retorno = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<String>(requestHeaders), String.class);
        }catch (Exception e){
            return false;
        }

        // o servidor retorna Login caso a tentativa seja bem sucedida
        if("Login".equals(retorno.getBody())){
            return true;
        }

        return false;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

}
