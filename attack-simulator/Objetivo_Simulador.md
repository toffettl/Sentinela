 Sentinela — Simulador de Atacante

1. Visão geral

O **Simulador de Atacante** é o componente responsável por gerar os comportamentos usados nos testes do Sentinela.
Desenvolvido em Python, o simulador permite executar diferentes cenários de ataque, tanto individualmente quanto com vários usuários ao mesmo tempo.

2. Responsabilidade

O `attacker.py` tem como objetivo simular ações que representem comportamentos suspeitos.
A detecção e a análise desses comportamentos não ficam no simulador. Sua função é gerar as requisições necessárias para reproduzir os cenários durante os testes.

3. Cenários de ataque

O simulador possui cinco cenários individuais e um cenário combinado.

| Cenário             | Comportamento                                             |
| ------------------- | --------------------------------------------------------- |
| `brute-force`       | Realiza 5 tentativas de login com senha incorreta         |
| `unknown-ip`        | Realiza um login utilizando um IP gerado pelo simulador   |
| `off-hours`         | Realiza um login utilizando um horário fora do expediente |
| `admin-access`      | Executa uma requisição de acesso administrativo           |
| `abnormal-download` | Realiza 8 downloads em sequência                          |
| `combined`          | Executa os cenários anteriores em sequência               |

O cenário `combined` reúne os comportamentos em uma única execução, facilitando os testes do fluxo completo.

4. Múltiplos atacantes

O simulador permite executar o mesmo cenário com vários usuários.
O parâmetro `--attackers` define a quantidade de atacantes. Os usuários são selecionados pelo simulador e as execuções são feitas em paralelo.

Exemplo:
```bash
python attacker.py --target boneco --attackers 4 combined
```
Dessa forma, é possível testar o comportamento do sistema com mais de um usuário realizando ataques simultaneamente.

 5. Geração de IP

O simulador possui uma função para gerar endereços IP utilizados nos testes.
São considerados dois tipos de situação:

```text
IP conhecido
IP desconhecido
```

6. Comunicação
O `attacker.py` utiliza a biblioteca `requests` para realizar requisições HTTP.

7. Execução

Alguns exemplos de execução:

```bash
# ataque de força bruta
python attacker.py --target boneco brute-force

# acesso administrativo
python attacker.py --target boneco admin-access

# downloads em sequência
python attacker.py --target boneco abnormal-download

# cenário combinado
python attacker.py --target boneco combined

# quatro atacantes executando em paralelo
python attacker.py --target boneco --attackers 4 combined
```

8. Tecnologias utilizadas

* Python 3
* requests
* argparse
* concurrent.futures
* ThreadPoolExecutor
* json
* datetime
* random

9. Resultado

Foram realizados testes com os diferentes cenários de ataque, incluindo execução individual e com múltiplos atacantes.

Os testes confirmaram o envio das requisições e a execução dos cenários previstos no simulador.

