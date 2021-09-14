package com.jvapp.meudinheiro.activity

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.jvapp.meudinheiro.model.Usuario
import android.os.Bundle
import com.jvapp.meudinheiro.R
import android.widget.Toast
import com.jvapp.meudinheiro.config.ConfiguracaoFirebase
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.jvapp.meudinheiro.helper.Base64Custom
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.android.material.textfield.TextInputEditText
import com.jvapp.meudinheiro.model.Movimentacao
import com.google.firebase.database.DatabaseReference
import com.jvapp.meudinheiro.helper.DateUtil
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import android.content.Intent
import com.jvapp.meudinheiro.activity.PrincipalActivity
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide
import com.jvapp.meudinheiro.activity.LoginActivity
import com.jvapp.meudinheiro.activity.CadastroActivity
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import androidx.recyclerview.widget.RecyclerView
import com.jvapp.meudinheiro.adapter.AdapterMovimentacao
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import android.content.DialogInterface
import android.view.View
import android.widget.Button
import com.jvapp.meudinheiro.activity.MainActivity
import com.jvapp.meudinheiro.activity.DespesasActivity
import com.jvapp.meudinheiro.activity.ReceitasActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener
import com.jvapp.meudinheiro.adapter.AdapterMovimentacao.MyViewHolder
import com.google.firebase.database.Exclude
import java.lang.Exception

class CadastroActivity : AppCompatActivity() {
    //Atributos para cadastro
    private var campoNome: EditText? = null
    private var campoEmail: EditText? = null
    private var campoSenha: EditText? = null
    private var buttonCadastrar: Button? = null

    //Atributo de Autenticação
    private var autenticacao: FirebaseAuth? = null
    private var usuario: Usuario? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)
        //Toobar
        supportActionBar!!.title = "Cadastro"

        // Recuperando os campos do Layout
        campoNome = findViewById(R.id.editNome)
        campoEmail = findViewById(R.id.editEmail)
        campoSenha = findViewById(R.id.editSenha)
        buttonCadastrar = findViewById(R.id.buttonCadastrar)

        // Aplicando a função click do Buttão Cadastro
        buttonCadastrar!!.setOnClickListener(View.OnClickListener {
            //Recupera o que usuario digitou e transforma em String
            val textoNome = campoNome!!.getText().toString()
            val textoEmail = campoEmail!!.getText().toString()
            val textoSenha = campoSenha!!.getText().toString()

            //Validar se os campos foram preenchidos
            if (!textoNome.isEmpty()) {
                if (!textoEmail.isEmpty()) {
                    if (!textoSenha.isEmpty()) {
                        usuario = Usuario()
                        usuario!!.nome = textoNome
                        usuario!!.email = textoEmail
                        usuario!!.senha = textoSenha
                        cadastrarUsuario()
                    } else {
                        Toast.makeText(
                            this@CadastroActivity, "Preencha a senha",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@CadastroActivity, "Preencha o email",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@CadastroActivity, "Preencha o nome",
                    Toast.LENGTH_LONG
                ).show()
            }
            //Fim da Validação
        })
    }

    //metedo para autentica e cadastra o usuario
    fun cadastrarUsuario() {
        autenticacao = ConfiguracaoFirebase.getAutenticacao()
        autenticacao!!.createUserWithEmailAndPassword(
            usuario!!.email.toString(), usuario!!.senha.toString()
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                //Salva usuario no banco de Dados
                val idUsuario = Base64Custom.codificarBase64(usuario!!.email)
                usuario!!.idUsuario = idUsuario
                usuario!!.salvar()
                finish()
            } else {
                var excecao = ""
                try {
                    throw task.exception!!
                } catch (e: FirebaseAuthWeakPasswordException) {
                    excecao = "Digite uma senha mais forte!"
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    excecao = "Por favor, digite um e-mail válido!"
                } catch (e: FirebaseAuthUserCollisionException) {
                    excecao = "Esse email ja foi cadastrado"
                } catch (e: Exception) {
                    excecao = "Erro ao cadastrar usuário: " + e.message
                    e.printStackTrace()
                }
                Toast.makeText(
                    this@CadastroActivity, excecao,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}