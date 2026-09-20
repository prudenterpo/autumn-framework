# language: pt
Funcionalidade: Descoberta de componentes
  Como desenvolvedor usando o Autumn
  Quero que o container ache só classes @Component no pacote base
  Para eu não registrar bean na mão

  Cenário: Encontra classe anotada com @Component
    Dado uma classe anotada com @Component no pacote base
    Quando o container faz o scan desse pacote
    Então essa classe entra no conjunto de componentes

  Cenário: Ignora classe sem @Component
    Dado uma classe no pacote base sem @Component
    Quando o container faz o scan desse pacote
    Então essa classe não entra no conjunto de componentes

  Cenário: Classe com dependência ausente é ignorada
    Dado um .class no pacote cuja carga lança ClassNotFoundException ou NoClassDefFoundError
    Quando o container faz o scan
    Então essa classe é ignorada
    E o scan dos demais continua

  Cenário: Falha inesperada no scan é logada em WARN
    Dado um .class cuja carga lança um erro que não é ClassNotFoundException nem NoClassDefFoundError
    Quando o container faz o scan
    Então o AutumnLogger emite WARN com o nome da classe
    E o scan dos demais continua

  Cenário: Scan cobre diretório e JAR
    Dado o pacote base presente em diretório de classes ou em JAR
    Quando o container faz o scan
    Então as @Component dos dois formatos são encontradas

  Cenário: start recebe um único pacote
    Dado um pacote base válido
    Quando eu chamo Autumn.start com esse pacote
    Então só esse pacote é varrido
