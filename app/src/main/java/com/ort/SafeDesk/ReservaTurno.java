package com.ort.SafeDesk;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.ort.SafeDesk.Interface.GetEdificios;
import com.ort.SafeDesk.Interface.GetHora;
import com.ort.SafeDesk.Interface.GetPisos;
import com.ort.SafeDesk.Interface.PostTurno;
import com.ort.SafeDesk.Interface.UsuariosDependiente;
import com.ort.SafeDesk.Model.Edificio;
import com.ort.SafeDesk.Model.Hora;
import com.ort.SafeDesk.Model.Piso;
import com.ort.SafeDesk.Model.TurnoBody;
import com.ort.SafeDesk.Model.UsuarioDep;
import com.ort.SafeDesk.utils.ApiUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservaTurno extends AppCompatActivity implements View.OnClickListener {

    private Spinner usuariosDP;
    private Spinner edificiosDP;
    private Spinner pisosDP;
    private Spinner horasDP;
    private ImageButton imageButton;
    private Button reserva;
    private EditText fecha;
    private int dia, mes, ano;
    private String fechaSelected;

    private List<Piso> pisos;
    private List<Hora> horas;
    private List<Edificio> edificios;
    private List<UsuarioDep> usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_turno);

        fecha = findViewById(R.id.editTextFecha);
        imageButton = findViewById(R.id.imageButton);
        reserva = findViewById(R.id.btn_reserva);
        usuariosDP = findViewById(R.id.spinner1);
        edificiosDP = findViewById(R.id.spinner2);
        pisosDP = findViewById(R.id.spinner3);
        horasDP = findViewById(R.id.spinner4);

        configUsuariosSpinner();
        imageButton.setOnClickListener(this);
        reserva.setOnClickListener(this);

       /*List<String> selFecha = new ArrayList<String>();
        selFecha.add("Selecciona Fecha");
        llenarSpinnersString(edificiosDP, selFecha);

        List<String> selEdi = new ArrayList<String>();
        selEdi.add("Selecciona Edificio");
        llenarSpinnersString(pisosDP, selEdi);
        llenarSpinnersString(horasDP, selEdi);*/

        usuariosDP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        edificiosDP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (edificios != null) {
                    configPisosSpinner(edificios.get((int) edificiosDP.getSelectedItemId()).getId());
                    configHorasSpinner(edificios.get((int) edificiosDP.getSelectedItemId()).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        pisosDP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        horasDP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(imageButton.equals(view)){
            Calendar c = Calendar.getInstance();
            dia = c.get(Calendar.DAY_OF_MONTH);
            mes = c.get(Calendar.MONTH);
            ano = c.get(Calendar.YEAR);

            DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    fecha.setText(day + "/" + (month+1) + "/" + year);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    //String currentDateandTime = sdf.format(new Date());
                    //String fecha = sdf.format(new Date(year, month, day));
                    fechaSelected = year + "-" + (month +1) + "-" + day;
                    //Date fecha = new Date(year, month, day);
                    configEdificiosSpinner(fechaSelected);
                }
            }
                    ,ano, mes, dia);
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePicker.show();
        }
        else if(reserva.equals(view)) {

            if (edificios != null && usuarios != null && pisos != null && horas != null) {
                saveTurno(new TurnoBody(usuarios.get((int) usuariosDP.getSelectedItemId()).getDni(),
                        fechaSelected, horas.get((int) horasDP.getSelectedItemId()).getId(),
                        pisos.get((int) pisosDP.getSelectedItemId()).getId(),
                        edificios.get((int) edificiosDP.getSelectedItemId()).getId()));
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Faltan seleccionar parametros!",Toast. LENGTH_SHORT).show();
            }

        }
        else{

        }
    }

    private void accessMainApp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void saveTurno(TurnoBody turnoNuevo){
        PostTurno postTurno = (PostTurno) ApiUtils.getAPI(PostTurno.class);

        Call<TurnoBody> call = postTurno.saveTurno(turnoNuevo);
        call.enqueue(new Callback<TurnoBody>()  {
            @Override
            public void onResponse(Call<TurnoBody> call, Response<TurnoBody> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),"Turno registrado correctamente!",Toast. LENGTH_SHORT).show();
                    accessMainApp();
                }
                else
                {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getApplicationContext(), jObjError.getString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TurnoBody> call, Throwable t) {
                Toast. makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configPisosSpinner(long idEdificio){
        GetPisos getPisos = (GetPisos)ApiUtils.getAPI(GetPisos.class);

        Call<List<Piso>> call = getPisos.getPisos(fechaSelected, idEdificio);

        call.enqueue(new Callback<List<Piso>>() {
            @Override
            public void onResponse(Call<List<Piso>> call, Response<List<Piso>> response) {
                pisos = response.body();
                List<String> pisosList = new ArrayList<String>();
                for (Piso p : pisos) {
                    pisosList.add(p.getNombre());
                }
                llenarSpinnersString(pisosDP, pisosList);
            }

            @Override
            public void onFailure(Call<List<Piso>> call, Throwable t) {

            }
        });
        }

    private void configHorasSpinner(long idEdificio){
        GetHora getHora = (GetHora)ApiUtils.getAPI(GetHora.class);

        Call<List<Hora>> call = getHora.getHoras(idEdificio, fechaSelected);

        call.enqueue(new Callback<List<Hora>>() {
            @Override
            public void onResponse(Call<List<Hora>> call, Response<List<Hora>> response) {
                horas = response.body();
                List<String> horasList = new ArrayList<String>();
                for (Hora h : horas) {
                    horasList.add(h.getHora());
                }
                llenarSpinnersString(horasDP, horasList);
            }

            @Override
            public void onFailure(Call<List<Hora>> call, Throwable t) {

            }
        });
    }

    private void configEdificiosSpinner(String fechaParam){

            GetEdificios getEdificios = (GetEdificios)ApiUtils.getAPI(GetEdificios.class);
            Call<List<Edificio>> call = getEdificios.getEdificios(fechaParam);

            call.enqueue(new Callback<List<Edificio>>() {
                @Override
                public void onResponse(Call<List<Edificio>> call, Response<List<Edificio>> response) {
                    edificios = response.body();
                    List<String> edificiosList = new ArrayList<String>();
                    for (Edificio e : edificios) {
                        edificiosList.add(e.getNombre() + " - " + e.getDireccion());
                    }
                    llenarSpinnersString(edificiosDP, edificiosList);
                }

                @Override
                public void onFailure(Call<List<Edificio>> call, Throwable t) {

                }
            });
        }

    private void configUsuariosSpinner(){
        UsuariosDependiente getUsuarios = (UsuariosDependiente)ApiUtils.getAPI(UsuariosDependiente.class);

        Call<List<UsuarioDep>> call = getUsuarios.getUsuarios();

        call.enqueue(new Callback<List<UsuarioDep>>() {
            @Override
            public void onResponse(Call<List<UsuarioDep>> call, Response<List<UsuarioDep>> response) {
                usuarios = response.body();
                List<String> usuariosList = new ArrayList<String>();
                for(UsuarioDep u : usuarios){
                    usuariosList.add(u.getNombre());
                }
                llenarSpinnersString(usuariosDP, usuariosList);
            }

            @Override
            public void onFailure(Call<List<UsuarioDep>> call, Throwable t) {

            }
        });

        }

        private void llenarSpinnersString(Spinner spinner, List<String> list){
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }

}