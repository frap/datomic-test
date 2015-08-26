(ns autodjinn.core-test
  (:require [clojure.test :refer :all]
            [autodjinn.core :refer :all]))


(deftest pairs-of-values
  (let [args ["--server" "localhost"
              "--port"   "8080"
              "--environment" "production"]]
    (is (= {:server "localhost"
            :port "8080"
            :environment "production"}
           (parse-args args)))
    ))

(deftest remove-brackets
  (let [test-id "<T<e>s<t><< i>>d<>"]
    (is (= "Test id"
           (remove-angle-brackets test-id)))))

(defn simple-content-type [full-content-type]
  (-> full-content-type
      (clojure.string/split #"[;]")
      (first)
      (clojure.string/lower-case)))

(defn is-content-type? [body requested-type]
  (= (simple-content-type (:content-type body))
     requested-type))

(defn find-body-of-type [bodies type]
  (:body (first #(is-content-type? %1 type) bodies)))

(defn get-text-body [msg]
  (find-body-of-type (message/message-body msg) "text/plain"))

(defn get-text-html [msg]
  (find-body-of-type (message/message-body msg) "text/html"))

(defn get-sent-date
  "Returns an instant for the date sent"
  [msg]
  (.getSentDate msg))

(defn get-received-date
  "Returns an instant for the date received"
  [msg]
  (.getReceivedDate msg))

(defn cc-list
  "Returns a sequence of CC-ed recipients"
  [msg]
  (map str
       (.getRecipients msg javax.mail.Message$RecipientType/CC)))

(defn bcc-list
  "Returns a sequence of BCC-ed recipients"
  [msg]
  (map str
       (.getRecipients msg javax.mail.Message$RecipientType/BCC)))
