# language: pt
Funcionalidade: Injeção por construtor
  Como desenvolvedor usando o Autumn
  Quero dependências resolvidas no construtor, com Primary e Qualifier
  Para montar o grafo sem new manual

  Cenário: Injeta dependência pelo único construtor
    Dado um bean cujo construtor pede outro bean registrado
    Quando eu peço o bean pelo tipo
    Então a dependência vem preenchida

  Cenário: Vários construtores exigem @Inject
    Dado uma @Component com mais de um construtor e nenhum @Inject
    Quando o container registra a classe
    Então o registro falha com mensagem pedindo @Inject

  Cenário: Interface com uma implementação
    Dado uma interface com uma única @Component implementando
    Quando eu peço a interface
    Então recebo essa implementação

  Cenário: Várias implementações sem @Primary nem @Qualifier
    Dado uma interface com duas @Component e nenhuma @Primary
    Quando o container registra a segunda
    Então lança NoUniqueBeanException
    E a mensagem cita as duas implementações

  Cenário: @Primary escolhe a implementação
    Dado uma interface com duas @Component e uma marcada @Primary
    Quando eu peço a interface
    Então recebo a implementação @Primary

  Cenário: Duas @Primary na mesma interface
    Dado duas @Component @Primary para a mesma interface
    Quando o container registra a segunda
    Então lança NoUniqueBeanException

  Cenário: Singleton é a instância padrão
    Dado um bean sem @Scope
    Quando eu peço o tipo duas vezes
    Então recebo a mesma instância

  Cenário: Prototype devolve instância nova
    Dado um bean com @Scope("prototype")
    Quando eu peço o tipo duas vezes
    Então recebo instâncias diferentes

  Cenário: Grafo de singletons sobe sem deadlock
    Dado singleton A cujo construtor pede singleton B
    Quando eu peço A
    Então A e B nascem
    E getBean(A) depois devolve a mesma A

  Cenário: Dependência circular em singleton
    Dado singleton A pedindo B e singleton B pedindo A
    Quando eu peço A
    Então lança CircularDependencyException
    E a mensagem cita a classe em criação

  Cenário: Dependência circular em prototype
    Dado prototype A pedindo B e prototype B pedindo A
    Quando eu peço A
    Então lança CircularDependencyException

  Cenário: @Qualifier escolhe pelo nome
    Dado EmailSender com @Component("email") e SmsSender com @Component("sms")
    E um construtor com parâmetro @Qualifier("email")
    Quando o bean dono do construtor é criado
    Então a dependência é o EmailSender

  Cenário: @Qualifier ganha de @Primary
    Dado uma impl @Primary e outra nomeada "sms"
    E um construtor com @Qualifier("sms")
    Quando o bean é criado
    Então a dependência é a impl "sms"
    E não a @Primary
