package com.example.instagram.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.instagram.R;
import com.example.instagram.activity.EditarPerfilActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {

    private ProgressBar progressBar;
    private CircleImageView imagePerfil;
    public GridView gridViewPerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private Button buttonAcaoPerfil;

    public PerfilFragment() {
        // Required empty public constructor
    }


    public static PerfilFragment newInstance() {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_perfil, container, false);

        //Configurações dos componentes
        gridViewPerfil = view.findViewById(R.id.gridViewPerfil);
        progressBar = view.findViewById(R.id.progressBarPerfil);
        imagePerfil = view.findViewById(R.id.imageEditarPerfil);
        textPublicacoes = view.findViewById(R.id.textPublicacoes);
        textSeguidores = view.findViewById(R.id.textSeguidores);
        textSeguindo = view.findViewById(R.id.textSeguindo);
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcaoPerfil);

        //abrir a edição de perfil
        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity(i);
            }
        });

        return view;
    }
}