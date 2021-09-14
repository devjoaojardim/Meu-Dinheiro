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
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.jvapp.meudinheiro.activity.MainActivity
import com.jvapp.meudinheiro.activity.DespesasActivity
import com.jvapp.meudinheiro.activity.ReceitasActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener
import com.jvapp.meudinheiro.adapter.AdapterMovimentacao.MyViewHolder
import com.google.firebase.database.Exclude
import java.text.DecimalFormat
import java.util.ArrayList

class PrincipalActivity : AppCompatActivity() {
    private var calendarView: MaterialCalendarView? = null
    private var textSaudacao: TextView? = null
    private var textSaldo: TextView? = null
    private var despesaTotal: Double? = 0.0
    private var receitaTotal: Double? = 0.0
    private var resumoUsuario = 0.0
    private val firebaseRef = ConfiguracaoFirebase.getFirebase()
    private val auth = ConfiguracaoFirebase.getAutenticacao()
    private var usuarioRef: DatabaseReference? = null
    private var valueEventListenerUsuario: ValueEventListener? = null
    private var valueEventListenerMovimentacoes: ValueEventListener? = null
    private var recyclerView: RecyclerView? = null
    private var adapterMovimentacao: AdapterMovimentacao? = null
    private val movimentacoes: MutableList<Movimentacao> = ArrayList()
    private var movimentacao: Movimentacao? = null
    private var movimentacaoRef: DatabaseReference? = null
    private var mesAnoSelecionado: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = " "
        setSupportActionBar(toolbar)


        //__________________________ TOOLBAR __________________________________________________//
        textSaldo = findViewById(R.id.textSaldo)
        textSaudacao = findViewById(R.id.textSaudacao)
        calendarView = findViewById(R.id.calendarView)
        recyclerView = findViewById(R.id.recyclerMovimentos)
        configuraCalendarView()
        swipe()


        //Configurar Adapter
        adapterMovimentacao = AdapterMovimentacao(movimentacoes, this)


        //Configurar RecyclerView
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView!!.setLayoutManager(layoutManager)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.setAdapter(adapterMovimentacao)


        //Calendario
    }

    //metodo Swipe
    fun swipe() {
        val itemTouch: ItemTouchHelper.Callback = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = ItemTouchHelper.ACTION_STATE_IDLE
                val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                excluirMovimentacao(viewHolder)
            }
        }
        ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView)
    }

    fun excluirMovimentacao(viewHolder: RecyclerView.ViewHolder) {
        val alertDialog = AlertDialog.Builder(this)

        //Coonfigurar Dialog
        alertDialog.setTitle("Excluir Movimentação da Conta")
        alertDialog.setMessage("Você tem certeza que deseja realmente excluir essa movimentação de sua conta?")
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Confirmar") { dialog, which ->
            val position = viewHolder.adapterPosition
            movimentacao = movimentacoes[position]
            val emailUsuario = auth.currentUser!!.email
            val idUsuario = Base64Custom.codificarBase64(emailUsuario)
            movimentacaoRef = firebaseRef.child("movimentacao")
                .child(idUsuario!!)
                .child(mesAnoSelecionado!!.replace("/", ""))
            movimentacaoRef!!.child(movimentacao!!.key.toString()).removeValue()
            adapterMovimentacao!!.notifyItemRemoved(position)
            atualizarSaldo()
        }
        alertDialog.setNegativeButton("Cancelar") { dialog, which ->
            Toast.makeText(
                this@PrincipalActivity,
                "Cancelada a opção de Excluir",
                Toast.LENGTH_LONG
            ).show()
            adapterMovimentacao!!.notifyDataSetChanged()
        }
        val alert = alertDialog.create()
        alert.show()
    }

    fun atualizarSaldo() {
        val emailUsuario = auth.currentUser!!.email
        val idUsuario = Base64Custom.codificarBase64(emailUsuario)
        usuarioRef = firebaseRef.child("usuarios").child(idUsuario!!)
        if (movimentacao!!.tipo == "r") {
            receitaTotal = receitaTotal!! - movimentacao!!.valor
            usuarioRef!!.child("receitaTotal").setValue(receitaTotal)
        }
        if (movimentacao!!.tipo == "d") {
            despesaTotal = despesaTotal!! - movimentacao!!.valor
            usuarioRef!!.child("despesaTotal").setValue(despesaTotal)
        }
    }

    //Metodo para listagem de movimentos
    fun recuperarMovimentacoes() {
        val emailUsuario = auth.currentUser!!.email
        val idUsuario = Base64Custom.codificarBase64(emailUsuario)
        movimentacaoRef = firebaseRef.child("movimentacao")
            .child(idUsuario!!)
            .child(mesAnoSelecionado!!.replace("/", ""))
        Log.i("FUNCIONANDO", mesAnoSelecionado!!.replace("/", "") + " -> " + idUsuario)
        valueEventListenerMovimentacoes =
            movimentacaoRef!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.i("FUNCIONANDO", "Erro -> 1")
                    movimentacoes.clear()
                    for (dados in dataSnapshot.children) {
                        val movimentacao = dados.getValue(Movimentacao::class.java)
                        movimentacao!!.key = dados.key
                        movimentacoes.add(movimentacao)
                    }
                    adapterMovimentacao!!.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.i("FUNCIONANDO", "Erro -> " + databaseError.message)
                }
            })
    }

    //metodo para mostra o resumo
    fun recuperaResumo() {
        val emailUsuario = auth.currentUser!!.email
        val idUsuario = Base64Custom.codificarBase64(emailUsuario)
        usuarioRef = firebaseRef.child("usuarios").child(idUsuario!!)
        Log.i("onStop", "evento foi Adicionado")
        valueEventListenerUsuario = usuarioRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario = snapshot.getValue(Usuario::class.java)
                despesaTotal = usuario!!.despesaTotal
                receitaTotal = usuario!!.receitaTotal
                resumoUsuario = receitaTotal!! - despesaTotal!!
                val decimalFormat = DecimalFormat("0.##")
                val resultadoFormatado = decimalFormat.format(resumoUsuario)
                textSaudacao!!.text = "Olá, " + usuario.nome
                textSaldo!!.text = "R$ $resultadoFormatado"
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    //Metodos para Menu Sair
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSair -> {
                auth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //Metedos para Acionar as Activity
    fun adicionarDespesa(view: View?) {
        startActivity(Intent(this, DespesasActivity::class.java))
    }

    fun adicionarReceita(view: View?) {
        startActivity(Intent(this, ReceitasActivity::class.java))
    }

    fun configuraCalendarView() {
        val meses = arrayOf<CharSequence>(
            "Jan",
            "Fev",
            "Mar",
            "Abr",
            "Mai",
            "Jun",
            "Jul",
            "Ago",
            "Set",
            "Out",
            "Nov",
            "Dez"
        )
        calendarView!!.setTitleMonths(meses)
        val dataAtual = calendarView!!.currentDate
        val mesSelecionado = String.format("%02d", dataAtual.month + 1)
        mesAnoSelecionado = mesSelecionado + "/" + dataAtual.year
        calendarView!!.setOnMonthChangedListener { widget, date ->
            val mesSelecionado = String.format("%02d", date.month + 1)
            mesAnoSelecionado = mesSelecionado + "/" + date.year
            movimentacaoRef!!.removeEventListener(valueEventListenerMovimentacoes!!)
            recuperarMovimentacoes()
        }
    }

    override fun onStart() {
        super.onStart()
        recuperaResumo()
        recuperarMovimentacoes()
    }

    override fun onStop() {
        super.onStop()
        Log.i("onStop", "evento foi removido")
        usuarioRef!!.removeEventListener(valueEventListenerUsuario!!)
        movimentacaoRef!!.removeEventListener(valueEventListenerMovimentacoes!!)
    }
}