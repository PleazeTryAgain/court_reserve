function fn() {
  var config = {
    baseUrl: java.lang.System.getProperty('courtreserve.test.baseUrl', 'http://localhost:8080')
  };
  var LocalDate = Java.type('java.time.LocalDate');
  config.futureDate = LocalDate.now().plusDays(2).toString();
  var UUID = Java.type('java.util.UUID');
  config.uniqueEmail = 'karate-' + UUID.randomUUID().toString() + '@example.com';
  return config;
}
