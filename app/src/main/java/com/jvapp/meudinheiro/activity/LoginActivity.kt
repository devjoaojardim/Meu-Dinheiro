package com.jvapp.meudinheiro.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.jvapp.meudinheiro.R;
import com.jvapp.meudinheiro.config.ConfiguracaoFirebase;
import com.jvapp.meudinheiro.model.Usuario;

public class LoginActivity extends AppCompatActivity {
    //Atributos para entra no Aplicativo
    private EditText campoEmail, campoSenha;
    private Button buttonEntrar;
    private Usuario usuario;
    private TextView textEsqueceuSenha;
    private FirebaseAuth autenticacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Recuperar do Layout
        campoEmail      = findViewById(R.id.editEmail);
        campoSenha      = findViewById(R.id.editSenha);
        buttonEntrar    = findViewById(R.id.buttonEntrar);
        textEsqueceuSenha = findViewById(R.id.textEsqueceuSenha);

        //Adicionar evento de click para entrar
        buttonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();
                if(!textoEmail.isEmpty()){
                    if(!textoSenha.isEmpty()){

                        usuario = new Usuario();
                        usuario.setEmail( textoEmail );
                        usuario.setSenha( textoSenha );
                        validarLogin();


                    }else{
                        Toast.makeText(LoginActivity.this,"Preencha a senha",
                                Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(LoginActivity.this,"Preencha o email",
                            Toast.LENGTH_LONG).show();
                }
                //Fim da Validação

            }
        });
        //Click para Esqueceu a senha
        textEsqueceuSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autenticacao = ConfiguracaoFirebase.getAutenticacao();
                final String textoEmail = campoEmail.getText().toString();
                usuario = new Usuario();
                usuario.setEmail( textoEmail );

                autenticacao.sendPasswordResetEmail(textoEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this,"Redefinicão de senha Envia para: " +textoEmail,
                                            Toast.LENGTH_LONG).show();
                                }else{
                                    String excecao = "";
                                    try {
                                        throw task.getException();
                                    }catch (FirebaseAuthInvalidUserException e) {
                                        excecao = "Usuário não está cadastrado.";
                                    }catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(LoginActivity.this,"Usuário não está cadastrado." ,
                                            Toast.LENGTH_LONG).show();
                                }

                            }
                        });


            }
        });


        //Fim dos eventos de click
    }
    //Criar metedo para valida a entrada do usuario
    public void validarLogin(){

        autenticacao = ConfiguracaoFirebase.getAutenticacao();
        autenticacao.signInWithEmailAndPassword(
               usuario.getEmail(),
               usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    abrirTelaPrincipal();

                }else{
                    String excecao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e) {
                        excecao = "Usuário não está cadastrado.";
                    }catch (FirebaseAuthInvalidCredentialsException e) {
                        excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                    }catch (Exception e){
                        excecao = "Erro ao fazer Login " + e.getMessage();
                        e.printStackTrace();
                    }


                    Toast.makeText(LoginActivity.this,excecao,
                            Toast.LENGTH_LONG).show();
                }
            }
        });


    }
    //metodo para abrir a tela Principal
    public void abrirTelaPrincipal(){
        startActivity(new Intent(this,PrincipalActivity.class));
        finish();
    }

}