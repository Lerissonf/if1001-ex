package br.ufpe.cin.if1001.rss;

/**
 * Created by LERISSON on 02/04/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

class CustomAdapter extends BaseAdapter{
    //private List<ItemRSS> itens;
    Context contexto;
    List<ItemRSS> itens;


    public CustomAdapter(Context contexto, List<ItemRSS> item) {
        this.contexto = contexto;
        this.itens = item;
    }

    @Override
    public int getCount() {
        return itens.size();
    }

    @Override
    public Object getItem(int i) {
        return itens.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {


        View v;
        if( convertView == null) {
            v = LayoutInflater.from(contexto).inflate(R.layout.itemlista, parent, false);
        }
        else {
            v = convertView;}

        //Buscando a referência ao TextView para inserirmos o titulo
        TextView itemTitulo = (TextView) v.findViewById(R.id.item_titulo);
        //Buscando a referência ao TextView para inserirmos a data do item
        TextView itemData = (TextView) v.findViewById(R.id.item_data);

        LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.list);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(contexto, WebViewActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                contexto.startActivity(it);
            }
        });

       //get para retornar os titulos e as datas]
        Log.d("lerisson","ITEM -> "+i);
        String data = itens.get(i).getPubDate();
        String titulo = itens.get(i).getTitle();


        //Efetivamente setando o data e titulo na View
        itemData.setText(data);
        itemTitulo.setText(titulo);
        return v;
    }



}