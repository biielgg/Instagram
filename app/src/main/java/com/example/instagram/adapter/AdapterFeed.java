package com.example.instagram.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Feed;
import com.example.instagram.model.PostagemCurtida;
import com.example.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.MyViewHolder> {

    private List<Feed> listaFeed;
    private Context context;

    public AdapterFeed(List<Feed> listaFeed, Context context) {
        this.listaFeed = listaFeed;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View itemLista = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_feed, viewGroup, false);
        return new AdapterFeed.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(AdapterFeed.MyViewHolder myViewHolder, int i) {

        final Feed feed = listaFeed.get(i);
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //carrega dados do feed
        Uri uriFotoUsuario = Uri.parse(feed.getFotoUsuario());
        Uri uriFotoPostagem = Uri.parse(feed.getFotoPostagem());

        Glide.with(context).load(uriFotoUsuario).into(myViewHolder.fotoPerfil);
        Glide.with(context).load(uriFotoPostagem).into(myViewHolder.fotoPostagem);

        myViewHolder.descricao.setText(feed.getDescricao());
        myViewHolder.nome.setText(feed.getNomeusuario());

        /*
        postagens-curtidas
            -id_postagem
                -qtdCurtidas
                -id_usuario
                    nome usuario
                    caminho foto
         */
        //recuperar dados da postagem curtida

        DatabaseReference curtidasRef = ConfiguracaoFirebase.getFirebase()
                .child("postagens-curtidas")
                .child(feed.getId());
        curtidasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int qtdCurtidas = 0;
                if(dataSnapshot.hasChild("qtdCurtidas")){
                    PostagemCurtida postagemCurtida = dataSnapshot.getValue(PostagemCurtida.class);
                    qtdCurtidas = postagemCurtida.getQtdCurtidas();
                }

                //verifica se ja foi clicado
                if(dataSnapshot.hasChild(usuarioLogado.getId())){
                    myViewHolder.likeButton.setLiked(true);
                } else {
                    myViewHolder.likeButton.setLiked(false);
                }

                //monta objetivo postagem curtida
                PostagemCurtida curtida = new PostagemCurtida();
                curtida.setFeed(feed);
                curtida.setUsuario(usuarioLogado);
                curtida.setQtdCurtidas(qtdCurtidas);

                //Adciona eventos para curtir uma foto
                myViewHolder.likeButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        curtida.salvar();
                        myViewHolder.qtdCurtidas.setText(curtida.getQtdCurtidas() + " curtidas");
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        curtida.remover();
                        myViewHolder.qtdCurtidas.setText(curtida.getQtdCurtidas() + " curtidas");
                    }
                });

                myViewHolder.qtdCurtidas.setText(curtida.getQtdCurtidas() + " curtidas");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listaFeed.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView fotoPerfil;
        TextView nome, descricao, qtdCurtidas;
        ImageView fotoPostagem, visualizarComentario;
        LikeButton likeButton;

        public MyViewHolder (View itemView){
            super(itemView);

            fotoPerfil          = itemView.findViewById(R.id.imagePerfilPostagem);
            fotoPostagem        = itemView.findViewById(R.id.imagePostagemSelecionada);
            nome                = itemView.findViewById(R.id.textPerfilPostagem);
            qtdCurtidas         = itemView.findViewById(R.id.textQtdCurtidasPostagem);
            descricao           = itemView.findViewById(R.id.textDescricaoPostagem);
            visualizarComentario = itemView.findViewById(R.id.imageComentarioFeed);
            likeButton          = itemView.findViewById(R.id.likeButtonFeed);
        }
    }
}
