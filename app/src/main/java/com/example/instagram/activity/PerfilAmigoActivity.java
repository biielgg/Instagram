package com.example.instagram.activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;
    private Button buttonAcaoPerfil;
    private CircleImageView imagePerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;

    DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference seguidoresRef;
    private ValueEventListener valueEventListenerPerfilAmigo;

    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        //Configurações iniciais

        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");
        seguidoresRef = firebaseRef.child("seguidores");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        //inicialiar componentes
        inicializarComponentes();

        //Configura a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Recuperar usuario selecionado
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            //Configura nome do usuário na toolbar
            getSupportActionBar().setTitle(usuarioSelecionado.getNome());

            //recupera foto do usuario
            String caminhoFoto = usuarioSelecionado.getCaminhoFoto();
            if (caminhoFoto != null){
                Uri url = Uri.parse(caminhoFoto);
                Glide.with(PerfilAmigoActivity.this)
                        .load(url)
                        .into(imagePerfil);
            }
        }
    }

    public void carregarFotosPostagem(){
        
    }

    private void recuperarDadosUsuarioLogado(){
        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //recupera dados do usuario logado
                        usuarioLogado = dataSnapshot.getValue(Usuario.class);
                        verificaSegueUsuarioAmigo();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    private void verificaSegueUsuarioAmigo(){
        DatabaseReference seguidorRef = seguidoresRef
                .child(idUsuarioLogado)
                .child(usuarioSelecionado.getId());
        seguidorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){//ja ta sendo seguido
                    habilitarBotaoSeguir(true);
                } else {//ainda não está seguindo
                    habilitarBotaoSeguir(false);
                    buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //salvar seguidor
                            salvarSeguidor(usuarioLogado, usuarioSelecionado);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void habilitarBotaoSeguir(boolean segueUsuario){
        if(segueUsuario){
            buttonAcaoPerfil.setText("Seguindo");
        } else {
            buttonAcaoPerfil.setText("Seguir");
        }

    }

    private void salvarSeguidor(Usuario uLogado, Usuario uAmigo){
        HashMap<String, Object> dadosAmigo = new HashMap<>();
        dadosAmigo.put("nome", uAmigo.getNome());
        dadosAmigo.put("caminhoFoto", uAmigo.getCaminhoFoto());
        DatabaseReference seguidorRef = seguidoresRef
                .child(uLogado.getId())
                .child(uAmigo.getId());
        seguidorRef.setValue(dadosAmigo);

        //alterar o botão ação para seguindo
        buttonAcaoPerfil.setText("Seguindo");
        buttonAcaoPerfil.setOnClickListener(null);

        //incrementar seguindo do usuario logado
        int seguindo = uLogado.getSeguindo() +1;
        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo", seguindo);
        DatabaseReference usuarioSeguindo = usuariosRef
                .child(uLogado.getId());
        usuarioSeguindo.updateChildren(dadosSeguindo);

        //incrementar seguidores do amigo
        int seguidores = uAmigo.getSeguidores() +1;
        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores", seguidores);
        DatabaseReference usuarioSeguidores = usuariosRef
                .child(uAmigo.getId());
        usuarioSeguidores.updateChildren(dadosSeguidores);

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarDadosPerfilAmigo();
        recuperarDadosUsuarioLogado();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioAmigoRef.removeEventListener(valueEventListenerPerfilAmigo);
    }

    private void recuperarDadosPerfilAmigo(){
        usuarioAmigoRef = usuariosRef.child(usuarioSelecionado.getId());
        valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Usuario usuario = dataSnapshot.getValue(Usuario.class);

                        String postagens = String.valueOf(usuario.getPostagens());
                        String seguindo = String.valueOf(usuario.getSeguindo());
                        String seguidores = String.valueOf(usuario.getSeguidores());

                        textPublicacoes.setText(postagens);
                        textSeguindo.setText(seguindo);
                        textSeguidores.setText(seguidores);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    private void inicializarComponentes(){
        buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        imagePerfil = findViewById(R.id.imageEditarPerfil);
        textPublicacoes = findViewById(R.id.textPublicacoes);
        textSeguidores = findViewById(R.id.textSeguidores);
        textSeguindo = findViewById(R.id.textSeguindo);
        buttonAcaoPerfil.setText("Carregando");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}