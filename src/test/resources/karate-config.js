function fn() {
  var LocalDate = Java.type('java.time.LocalDate');
  var UUID = Java.type('java.util.UUID');

  var config = {
    baseUrl: karate.properties['courtreserve.test.baseUrl'] || 'http://localhost:8080'
  };

  config.futureDate = LocalDate.now().plusDays(2).toString();
  config.pastDate = LocalDate.now().minusDays(1).toString();
  config.uniqueEmail = 'karate-' + UUID.randomUUID().toString() + '@example.com';
  config.idInexistente = 999999;

  karate.configure('connectTimeout', 5000);
  karate.configure('readTimeout', 10000);

  return config;
}
