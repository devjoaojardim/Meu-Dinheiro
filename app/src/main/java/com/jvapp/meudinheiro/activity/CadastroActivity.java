package com.jvapp.meudinheiro.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.jvapp.meudinheiro.R;
import com.jvapp.meudinheiro.config.ConfiguracaoFirebase;
import com.jvapp.meudinheiro.model.Usuario;

public class CadastroActivity extends AppCompatActivity {
    //Atributos para cadastro
    private EditText campoNome, campoEmail, campoSenha;
    private Button buttonCadastrar;
   //Atributo de Autenticação
   private FirebaseAuth autenticacao;
   private Usuario usuario;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        // Recuperando os campos do Layout
        campoNome       = findViewById(R.id.editNome);
        campoEmail      = findViewById(R.id.editEmail);
        campoSenha      = findViewById(R.id.editSenha);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);

       // Aplicando a função click do Buttão Cadastro
       buttonCadastrar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //Recupera o que usuario digitou e transforma em String
                String textoNome = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

              //Validar se os campos foram preenchidos
               if(!textoNome.isEmpty()){
                   if(!textoEmail.isEmpty()){
                       if(!textoSenha.isEmpty()){

                           usuario = new Usuario();
                           usuario.setNome(textoNome);
                           usuario.setEmail(textoEmail);
                           usuario.setSenha(textoSenha);
                           cadastrarUsuario();


                       }else{
                           Toast.makeText(CadastroActivity.this,"Preencha a senha",
                                   Toast.LENGTH_LONG).show();
                       }

                   }else{
                       Toast.makeText(CadastroActivity.this,"Preencha o email",
                               Toast.LENGTH_LONG).show();
                   }

               }else{
                   Toast.makeText(CadastroActivity.this,"Preencha o nome",
                           Toast.LENGTH_LONG).show();

               }
               //Fim da Validação

           }
       });
    }
    //metedo para autentica e cadastra o usuario
    public void cadastrarUsuario(){

        autenticacao = ConfiguracaoFirebase.getAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    finish();
                }else{
                    String excecao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        excecao = "Digite uma senha mais forte!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Por favor, digite um e-mail válido!";

                    }catch (FirebaseAuthUserCollisionException e){
                        excecao = "Esse email ja foi cadastrado";

                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this,excecao,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}