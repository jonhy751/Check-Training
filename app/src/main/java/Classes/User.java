package Classes;

public class User {
    String email, senha, nome,flag, instituição;

    public User(String email, String senha, String nome, String flag, String instituição) {
        this.email = email;
        this.senha = senha;
        this.nome = nome;
        this.flag = flag;
        this.instituição =instituição;
    }

    public String getInstituição() {
        return instituição;
    }

    public void setInstituição(String instituição) {
        this.instituição = instituição;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
