package com.example.ice.myapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Button btnInserir;
    private Button btnListar;
    private RecyclerView rclLivros;
    private BibliotecaDbHelper dbHelper;
    private LivroAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new BibliotecaDbHelper(getApplicationContext());
        adapter = new LivroAdapter(getCursorLivrosPos1950());

        rclLivros = (RecyclerView) findViewById(R.id.rcl_livros);
        rclLivros.setLayoutManager(new LinearLayoutManager(this));
        rclLivros.setAdapter(adapter);

        final Random rnd = new Random();

        btnInserir = (Button) findViewById(R.id.btn_inserir);
        btnInserir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(BibliotecaContract.Livro.COLUMN_NAME_TITULO,"Livro " +rnd.nextInt());
                values.put(BibliotecaContract.Livro.COLUMN_NAME_AUTOR,"Autor " +rnd.nextInt());
                values.put(BibliotecaContract.Livro.COLUMN_NAME_ANO, 1900+rnd.nextInt(118));
                long id = db.insert(BibliotecaContract.Livro.TABLE_NAME,null, values);
                Log.i("DBINFO","registro criado com id: " + id);
                adapter.setCursor(getCursorLivrosPos1950());
            }
        });

        btnListar = (Button) findViewById(R.id.btn_listar);
        btnListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = getCursorLivrosPos1950();
                cursor.moveToPosition(-1);
                while(cursor.moveToNext()){
                    int idxTitulo = cursor.getColumnIndexOrThrow(BibliotecaContract.Livro.COLUMN_NAME_TITULO);
                    String titulo = cursor.getString(idxTitulo);
                    int idxAutor = cursor.getColumnIndexOrThrow(BibliotecaContract.Livro.COLUMN_NAME_AUTOR);
                    String autor = cursor.getString(idxAutor);
                    int idxAno = cursor.getColumnIndexOrThrow(BibliotecaContract.Livro.COLUMN_NAME_ANO);
                    String ano = cursor.getString(idxAno);
                    Log.i("DBINFO","titulo: " + titulo +" autor: "+ autor + " ano: " + ano);
                }
            }
        });
    }

    private Cursor getCursorLivrosPos1950() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] visao = {
                BibliotecaContract.Livro.COLUMN_NAME_TITULO,BibliotecaContract.Livro.COLUMN_NAME_AUTOR, BibliotecaContract.Livro.COLUMN_NAME_ANO
        };
        String restricoes = BibliotecaContract.Livro.COLUMN_NAME_ANO + " > ?";
        String[] params = {"1950"};
        String sort = BibliotecaContract.Livro.COLUMN_NAME_ANO+ " DESC";
        return db.query(BibliotecaContract.Livro.TABLE_NAME,visao,restricoes,params,null,null,sort,null);
    }


}
