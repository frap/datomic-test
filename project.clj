(defproject autodjinn "0.1.0-SNAPSHOT"
  :description "An email analysis tool"
  :url "http://www.red-elvis.net/email"
  :license {:name "Eclipse Public License"
                  :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                           [clojure-mail "0.1.6"]
                           [com.datomic/datomic-pro "0.9.5078" :exclusions [joda-time]]
                           [jarohen/nomad "0.7.1"]
                           [org.postgresql/postgresql "9.3-1102-jdbc41"]]
  :repositories  {"my.datomic.com" {:url "https://my.datomic.com/repo" :creds :gpg}}
  )
