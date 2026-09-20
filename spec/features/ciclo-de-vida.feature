# language: pt
Funcionalidade: Ciclo de vida do bean
  Como desenvolvedor usando o Autumn
  Quero @PostConstruct e @PreDestroy previsíveis por scope
  Para inicializar e encerrar só o que o container realmente gerencia

  Cenário: @PostConstruct roda depois da injeção
    Dado um singleton com dependência e método @PostConstruct
    Quando o bean é criado
    Então a dependência já está preenchida
    E o @PostConstruct rodou exatamente uma vez

  Cenário: Scan único dos métodos de lifecycle
    Dado um bean com @PostConstruct e @PreDestroy no mesmo tipo
    Quando postConstruct é chamado
    Então cada método do tipo é inspecionado uma vez
    E o @PostConstruct rodou uma vez

  Cenário: @PreDestroy de singleton no close
    Dado um singleton com @PreDestroy
    Quando o contexto dá close
    Então o @PreDestroy rodou

  Cenário: Prototype não é rastreado para @PreDestroy
    Dado um prototype com @PreDestroy
    Quando eu peço o bean e depois dou close no contexto
    Então o @PreDestroy não rodou

  Cenário: Prototype não nasce no bootstrap
    Dado um pacote com singleton e prototype
    Quando eu chamo Autumn.start
    Então o singleton já existe
    E nenhuma instância do prototype foi criada
    E getBean do prototype cria a primeira instância na hora

  Cenário: Falha em @PostConstruct não derruba os outros beans
    Dado dois singletons no mesmo pacote e o @PostConstruct de um lança
    Quando eu chamo Autumn.start
    Então a falha é logada via AutumnLogger
    E o outro singleton continua acessível no contexto
