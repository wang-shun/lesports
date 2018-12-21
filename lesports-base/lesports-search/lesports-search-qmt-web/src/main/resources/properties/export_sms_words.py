import os
def transformLine(line):
        line = line.replace("\"", "").replace("\n", "").replace(", ", ",").replace(" ", ",")
        words = line.split(",")
        return words

def filterWords(words):
        new_word_list = []
        for word in words:
                if ((not word.isalpha()) and word not in new_word_list):
                        new_word_list.append(word)
        return new_word_list

def getNamesFromRepo(host, port, db, username, password, collection, field):
        fileName = "/letv/data/" + db + "_" + collection + "_" + field;
        os.system("/letv/apps/mongodb/bin/mongoexport --host=" + host + " --port=" + port + " --db=" + db + " --username=" + username + " --password=" + password + " --collection=" + collection + " -f \"" + field + "\" --type=csv>" + fileName)
        word_list = []
        file = open(fileName)
        for line in file:
                word_list.extend(transformLine(line))
        file.close()
        os.system("rm -f " + fileName)
        return word_list

word_list = []
word_list.extend(getNamesFromRepo("10.150.130.24", "9102", "sms", "lesports", "lesports", "players", "name"))
word_list.extend(getNamesFromRepo("10.150.130.24", "9102", "sms", "lesports", "lesports", "teams", "name"))
word_list.extend(getNamesFromRepo("10.150.130.24", "9102", "sms", "lesports", "lesports", "teams", "nickname"))


sms_words = open("/letv/data/sms_words", "w")
word_list = filterWords(word_list)
for word in word_list:
        print word
        sms_words.write(word + "\n")
sms_words.close()