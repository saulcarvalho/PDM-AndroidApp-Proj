package ipleiria.pdm.maintenanceapppdm.classes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ipleiria.pdm.maintenanceapppdm.R;

/**
 * Classe que permite adaptar a View da janela de procura de manutenções de modo a mostrar todos os
 * manutenções do respetivo utilizador
 */
public class ListViewAdapterMan extends BaseAdapter implements AdapterView.OnItemClickListener {
    private final ArrayList<Event> listMaintenances;
    private final Activity activity;

    /**
     * Construtor principal da classe
     */
    public ListViewAdapterMan(ArrayList<Event> listMaintenances, Activity activity) {
        this.listMaintenances = listMaintenances;
        this.activity = activity;
    }

    /**
     * Sobreposição da função getCount(), que vai buscar o tamanho da lista de manutencoes
     */
    @Override
    public int getCount() {
        return listMaintenances.size();
    }

    /**
     * Sobreposição do método getItem(), que permite devolver uma manutenção mediante uma dada
     * posição
     *
     * @param position - Posição
     */
    @Override
    public Object getItem(int position) {
        return listMaintenances.get(position);
    }

    /**
     * Sobreposição do método getItemId(), que permite devolver a posição de uma dada manutenção
     * na respetiva lista
     *
     * @param position - Posição
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Sobreposição do método getView(), que permite devolver uma view mediante a posição
     * na lista, o contexto da view e o seu grupo
     *
     * @param position    - Posição do objeto na lista
     * @param convertView - Contexto da view
     * @param parent      - Grupo da view
     */
    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).
                    inflate(R.layout.item_layout, parent, false);
        }
        // get current item to be displayed
        Event currentItem = (Event) getItem(position);
        // get the TextView for item number and item name
        TextView textViewItemNumber =
                convertView.findViewById(R.id.tvEventID);
        TextView textViewItemName =
                convertView.findViewById(R.id.tvEventDate);
        ImageView imageView = convertView.findViewById(R.id.iv);

        //sets the text for item number and item name from the current item object
        textViewItemNumber.setText(Integer.toString(currentItem.getID()));
        textViewItemName.setText(currentItem.getDate());
        Picasso.get()
                .load(currentItem.getImage().getImageUri())
                .into(imageView);
        return convertView;
    }

    /**
     * Sobreposição do método onItemClick(), que permite a deteção do clique numa respetiva view,
     * adquirida no método getView(), através do adaptador, da view,
     * da posição e da sua identificação
     *
     * @param parent   - Contexto da view
     * @param view     - View
     * @param position - Posição do objeto na lista
     * @param id       - Identificação da view
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View alertView = activity.getLayoutInflater().inflate(R.layout.event_dialog_box, null);

        builder.setView(alertView);
        final AlertDialog alertDialog = builder.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView title = alertView.findViewById(R.id.tvSearchEventTitle);
        int ID = listMaintenances.get(position).getID();
        title.setText(alertView.getResources().getString(R.string.txtLabelMan).concat(Integer.toString(ID)));

        TextView tvID = alertView.findViewById(R.id.tvSearchEventID);
        tvID.setText(R.string.txtAddEventID);
        TextView tvDisplayID = alertView.findViewById(R.id.tvSearchEventDisplayID);
        tvDisplayID.setText(Integer.toString(ID));
        Manage.getInstance().setLastID(ID);

        TextView tvDate = alertView.findViewById(R.id.tvSearchEventDate);
        tvDate.setText(R.string.txtDate);
        String date = listMaintenances.get(position).getDate();
        TextView tvDisplayDate = alertView.findViewById(R.id.tvSearchEventDisplayDate);
        tvDisplayDate.setText(date);

        TextView tvDesc = alertView.findViewById(R.id.tvSearchEventDesc);
        tvDesc.setText(R.string.txtDesc);
        String description = listMaintenances.get(position).getDescription();
        TextView tvDisplayDesc = alertView.findViewById(R.id.tvSearchEventDisplayDesc);
        tvDisplayDesc.setText(description);
        tvDisplayDesc.setMaxLines(5);
        tvDisplayDesc.setVerticalScrollBarEnabled(true);
        tvDisplayDesc.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        tvDisplayDesc.setMovementMethod(new ScrollingMovementMethod());
        Manage.getInstance().setOldFieldDesc(description);

        TextView tvDuration = alertView.findViewById(R.id.tvSearchEventDuration);
        tvDuration.setText(R.string.txtDuration);
        int duration = listMaintenances.get(position).getDuration();
        TextView tvDisplayDuration = alertView.findViewById(R.id.tvSearchEventDisplayDuration);
        tvDisplayDuration.setText(Integer.toString(duration));
        Manage.getInstance().setOldFieldDuration(duration);

        TextView tvCost = alertView.findViewById(R.id.tvSearchEventCost);
        tvCost.setText(R.string.txtCost);
        int cost = listMaintenances.get(position).getCost();
        TextView tvDisplayCost = alertView.findViewById(R.id.tvSearchEventDisplayCost);
        tvDisplayCost.setText(Integer.toString(cost));
        Manage.getInstance().setOldFieldCost(cost);

        ImageView ivEventImage = alertView.findViewById(R.id.ivEventImage);
        Picasso.get()
                .load(listMaintenances.get(position).getImage().getImageUri())
                .into(ivEventImage);

        ImageView closeButton = alertView.findViewById(R.id.btCloseSearchEvent);

        // passa a dialog box para a gestao para poder ser fechado mais tarde
        Manage.getInstance().setLastItemEdit(alertDialog);

        closeButton.setOnClickListener(v -> alertDialog.dismiss());
    }
}