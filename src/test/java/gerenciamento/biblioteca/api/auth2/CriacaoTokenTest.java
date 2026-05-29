package gerenciamento.biblioteca.api.auth2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CriacaoTokenTest {
    /*
     * Para criação de um token, devem ser passado os valores abaixo:
     *  - id
     *  - nome
     *  - e-mail
     *  - roles
     * Ao obter estes dados, é verificado se o usuário já possui um token, caso tenha, é retornado uma exception.
     * Com o decorrer, é verificado se o id realmente existe na tabela usuário e se bate com o e-mail ali cadastrado. Caso não, retorna uma exception.
     * Após a validação, é gerado os tokens de access e de refresh token.
     * Por fim, é salvo no banco de dados os seguintes dados:
     *  - id
     *  - nome
     *  - e-mail
     *  - refresh_token
     *  - situacao (criada inicialmente pelo banco como ativo)
     *  - vencimento (criada apenas pelo banco)
     * Logo após é apenas verificado se ocorreu tudo de acordo. Caso não, é retornado um exception.
     */

    @Test
    public void testCriacaoToken() {
    }
}
