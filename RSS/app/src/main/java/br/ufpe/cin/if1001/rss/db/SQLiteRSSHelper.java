package br.ufpe.cin.if1001.rss.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.if1001.rss.domain.ItemRSS;


public class SQLiteRSSHelper extends SQLiteOpenHelper {
    //Nome do Banco de Dados
    private static final String DATABASE_NAME = "rss";
    //Nome da tabela do Banco a ser usada
    public static final String DATABASE_TABLE = "items";
    //Versão atual do banco
    private static final int DB_VERSION = 1;

    //alternativa
    Context c;

    private SQLiteRSSHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        c = context;
    }

    private static SQLiteRSSHelper db;

    //Definindo Singleton
    public static SQLiteRSSHelper getInstance(Context c) {
        if (db == null) {
            db = new SQLiteRSSHelper(c.getApplicationContext());
        }
        return db;
    }

    //Definindo constantes que representam os campos do banco de dados
    public static final String ITEM_ROWID = RssProviderContract._ID;
    public static final String ITEM_TITLE = RssProviderContract.TITLE;
    public static final String ITEM_DATE = RssProviderContract.DATE;
    public static final String ITEM_DESC = RssProviderContract.DESCRIPTION;
    public static final String ITEM_LINK = RssProviderContract.LINK;
    public static final String ITEM_UNREAD = RssProviderContract.UNREAD;

    //Definindo constante que representa um array com todos os campos
    public final static String[] columns = {ITEM_ROWID, ITEM_TITLE, ITEM_DATE, ITEM_DESC, ITEM_LINK, ITEM_UNREAD};

    //Definindo constante que representa o comando de criação da tabela no banco de dados
    private static final String CREATE_DB_COMMAND = "CREATE TABLE " + DATABASE_TABLE + " (" +
            ITEM_ROWID + " integer primary key autoincrement, " +
            ITEM_TITLE + " text not null, " +
            ITEM_DATE + " text not null, " +
            ITEM_DESC + " text not null, " +
            ITEM_LINK + " text not null, " +
            ITEM_UNREAD + " boolean not null);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Executa o comando de criação de tabela
        db.execSQL(CREATE_DB_COMMAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //estamos ignorando esta possibilidade no momento
        throw new RuntimeException("nao se aplica");
    }

    //IMPLEMENTAR ABAIXO
    //Implemente a manipulação de dados nos métodos auxiliares para não ficar criando consultas manualmente
    public long insertItem(ItemRSS item) {
        //adicionando no banco os valores postos no content value
        return insertItemDados(item.getTitle(), item.getPubDate(), item.getDescription(), item.getLink());
    }

    private long insertItemDados(String title, String pubDate, String description, String link) {
        //pegar a instancia writable
        SQLiteDatabase db = getWritableDatabase();
        //
        ContentValues itemRssContentValues = new ContentValues();
        itemRssContentValues.put(ITEM_TITLE, title);
        itemRssContentValues.put(ITEM_DATE, pubDate);
        itemRssContentValues.put(ITEM_DESC, description);
        itemRssContentValues.put(ITEM_LINK, link);
        itemRssContentValues.put(ITEM_UNREAD, 1);

        return db.insert(DATABASE_TABLE, null, itemRssContentValues);
    }


    public ItemRSS getItemRSS(String link) throws SQLException {
        SQLiteDatabase banco = getReadableDatabase();
        String selection = ITEM_LINK + " = ?";
        String selectionArgs[] = {link};
        Cursor cursorGetItens = banco.query(DATABASE_TABLE,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                ITEM_DATE);

        // fazer consulta forçadamente
        if (cursorGetItens.getCount() > 0) {
            cursorGetItens.moveToFirst();
            return new ItemRSS(cursorGetItens.getString(cursorGetItens.getColumnIndexOrThrow(ITEM_TITLE)),
                    cursorGetItens.getString(cursorGetItens.getColumnIndexOrThrow(ITEM_LINK)),
                    cursorGetItens.getString(cursorGetItens.getColumnIndexOrThrow(ITEM_DATE)),
                    cursorGetItens.getString(cursorGetItens.getColumnIndexOrThrow(ITEM_DESC)));
        }
        return null;
    }
    //metodo para retornar itens não lidos

    public List<ItemRSS> getItems() throws SQLException {

            SQLiteDatabase banco = getReadableDatabase();

            String selection = ITEM_UNREAD + " = ?";
            String selectionArgs[] = {"1"};

            Cursor cursor = banco.query(
                    DATABASE_TABLE,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            List<ItemRSS> listaRss = new ArrayList<>();

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(ITEM_TITLE));
                    String link = cursor.getString(cursor.getColumnIndexOrThrow(ITEM_LINK));
                    String pubDate = cursor.getString(cursor.getColumnIndexOrThrow(ITEM_DATE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(ITEM_DESC));
                    ItemRSS itemRSS = new ItemRSS(title, link, pubDate, description);
                    listaRss.add(itemRSS);
                } while (cursor.moveToNext());

                return listaRss;
            }
            return null;
        }


    public boolean markAsUnread(String link) {

        SQLiteDatabase banco = getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_UNREAD, 1);

        String selection = ITEM_LINK + " = ?";
        String selectionArgs[] = { link };

        int count = banco.update(
                DATABASE_TABLE,
                contentValues,
                selection,
                selectionArgs
        );

        return count > 0;
    }

    public boolean markAsRead(String link) {
        SQLiteDatabase banco = getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_UNREAD, 0);

        String selection = ITEM_LINK + " = ?";
        String selectionArgs[] = { link };

        int count = banco.update(
                DATABASE_TABLE,
                contentValues,
                selection,
                selectionArgs
        );

        return count > 0;

    }

}


