package br.ufpe.cin.if1001.rss.util;

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

import br.ufpe.cin.if1001.rss.R;
import br.ufpe.cin.if1001.rss.domain.ItemRSS;

public class CustomAdapter extends BaseAdapter{
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

//

       //get para retornar os titulos e as datas]
        Log.d("lerisson","ITEM -> "+i);
        String data = itens.get(i).getPubDate();
        String titulo = itens.get(i).getTitle();
        //itens.get(i).getLink();
        //Efetivamente setando o data e titulo na View
        itemData.setText(data);
        itemTitulo.setText(titulo);
        return v;
    }
    public String getLink(int position){

             return itens.get(position).getLink();
    }


}