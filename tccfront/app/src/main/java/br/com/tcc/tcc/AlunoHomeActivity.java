package br.com.tcc.tcc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Classe responsável pro criar a home do Aluno
 */
public class AlunoHomeActivity extends AppCompatActivity {

    @Override
    //Cria a tela de acordo com o layout definido
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aluno_home);
    }
}
