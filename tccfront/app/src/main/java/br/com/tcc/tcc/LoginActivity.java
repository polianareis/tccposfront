package br.com.tcc.tcc;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.tcc.tcc.databinding.ActivityLoginBinding;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Tela de login com email e senha
 */
public class LoginActivity extends AppCompatActivity {
    //Declaração dos atributos
    private UserLoginTask mAuthTask = null;
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private LoginViewModel loginViewModel;  //permite isolar a regra de negócio da tela de login


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding da tela de Login com a regra de negócio
        loginViewModel = new LoginViewModel();
        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setViewModel(loginViewModel);
        // Executa Login
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        // Executa Login
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form); // Recupera o formulário da tela Login
        mProgressView = findViewById(R.id.login_progress); // Recupera a barra progressiva
    }

     /**
      * Método que efetua o Login
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Apaga as mensagens de validação dos campos e-mail e senha
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Recupera as informações do aluno (email e senha)
        String email = loginViewModel.getAluno().getEmail();
        String password = loginViewModel.getAluno().getSenha();

        boolean cancel = false;
        View focusView = null;

        //Realiza a validação do campo e-mail preenchimento obrigatório
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        //Realiza a validação do campo e-mail quando é preenchido com valor inválido
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        //Realiza a validação do campo senha preenchimento obrigatório
        if (TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }


        if (cancel) {
            // Exibe o Focus no campo que apresentou a mensagem
            focusView.requestFocus();
        } else {
            // Executa o Login
            showProgress(true);
            mAuthTask = new UserLoginTask();
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Valida se no e-mail informado contém o "@"
     * @param email
     * @return
     */
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    /**
     * Exibe a interface de Progresso e oculta o formulário de login.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Tarefa que executa o Login em segundo plano ao ser acionado o comando [Enter]
     *
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return loginViewModel.login();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            // Verifica se o e-mail informado é do Administrador para exibir a Home do Administrador,
            // caso contrário será exibido a Home do Aluno.
            if (success) {
                if("adm@gmail.com".equals(loginViewModel.getAluno().getEmail())){
                    homeAdm();
                }else{
                    homeAluno();
                }
             // Caso o e-mail ou senha informado sejam inválidos o sistema exibe a mensagem parametrizada
             // e exibe o Focus no campo correspondente.
            } else {
                mPasswordView.setError(getString(R.string.error_invalid_email));
                mPasswordView.requestFocus();
            }
        }

    }
    // Exibe a tela Home do Perfil do Aluno
    private void homeAluno(){
        Intent intent = new Intent(this,AlunoHomeActivity.class);
        super.startActivity(intent);
    }
    // Exibe a tela Home do Perfil do Administrador
    private void homeAdm(){
        Intent intent = new Intent(this,AdministradorHomeActivity.class);
        super.startActivity(intent);
    }
}

