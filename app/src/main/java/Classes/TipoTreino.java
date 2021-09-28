package Classes;

public class TipoTreino {
    private String TipoTreino;
    private String data, time, descriçao,local,obs;

    public String getTipoTreino() {
        return TipoTreino;
    }

    public void setTipoTreino(String tipoTreino) {
        TipoTreino = tipoTreino;
    }



    public String getDescriçao() {
        return descriçao;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public TipoTreino(String tipoTreino, String data, String time, String descriçao, String local, String obs) {
        TipoTreino = tipoTreino;
        this.data = data;
        this.time = time;
        this.descriçao = descriçao;
        this.local = local;
        this.obs = obs;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public void setDescriçao(String descriçao) {
        this.descriçao = descriçao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public TipoTreino() {
    }



}


