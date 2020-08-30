<template>
  <div class="ums-page">
    <div class="ums-header">
      <div>
        <span
          class="iconfont umail-zuojiantou-up return-row-padding"
          style="cursor: pointer;"
          @click="$router.go(-1)"
        ></span>
        <span v-if="msg.subject == ''">未命名-写邮件</span>
        <span v-else>{{ msg.subject }}-写邮件</span>
      </div>
    </div>
    <ums-loading
      msg="正在发送邮件"
      :value="msg.subject"
      :visible="sendLoadingVisibled"
    ></ums-loading>
    <el-row
      :gutter="20"
      style="height:60px;padding-top: 20px;padding-bottom: 10px;padding-left:20px;padding-right:20px;overflow: hidden;"
    >
      <el-col :span="2">
        <div>
          <el-button
            type="primary"
            :disabled="msg.to.length == 0"
            size="small"
            @click="sendMessage"
            ><span class="iconfont umail-send menu-padding"></span
            >发送</el-button
          >
        </div>
      </el-col>
      <el-col :span="2" v-if="way == 'send'">
        <div>
          <el-button type="primary" size="small" @click="saveToDraft"
            ><span class="iconfont umail-save menu-padding"></span
            >保存</el-button
          >
        </div>
      </el-col>
      <el-col :span="2">
        <div>
          <el-upload
            class="upload-demo"
            :action="uploadUrl"
            :on-change="handleChange"
            :auto-upload="true"
            :http-request="uploadSectionFile"
            :file-list="fileList"
          >
            <el-button size="small" type="primary"
              ><span class="iconfont umail-attachment menu-padding"></span
              >附件</el-button
            >
          </el-upload>
        </div>
      </el-col>
      <el-col :span="18">
        <div>
          <!-- <div v-for="file in fileList">{{file.raw.name}}</div> -->
        </div>
      </el-col>
    </el-row>
    <!--输入input-->
    <el-row
      :gutter="20"
      style="padding-top:10px;border-bottom: 1px solid #e5e5e5;padding-left:20px;padding-right:20px;"
    >
      <el-col :span="2">
        <div style="color:#84898a;">
          <span>收件人：</span>
        </div>
      </el-col>
      <el-col :span="22">
        <div style="display: flex;">
          <el-input
            style="width:300px;border: 0px;outline:none;"
            placeholder=""
            v-model="toInput"
            @change="toInputChange"
            :autofocus="true"
          >
          </el-input>
          <div style="width:100%;">
            <el-tag
              style="margin-right:10px;border-radius: 20px;"
              v-for="(tag, key) in msg.to"
              :key="key"
              size="small"
              closable
              @close="handleToClose(tag)"
              >{{ tag }}</el-tag
            >
          </div>
          <span
            class="iconfont umail-add return-row-padding"
            style="cursor: pointer;color:#409EFF;font-size:25px;"
            @click="showContactDialog"
            data-to="to"
          ></span>
        </div>
      </el-col>
    </el-row>
    <el-row
      :gutter="20"
      style="padding-top:10px;border-bottom: 1px solid #e5e5e5;padding-left:20px;padding-right:20px;"
    >
      <el-col :span="2">
        <div style="color:#84898a;">
          <span>抄送：</span>
        </div>
      </el-col>
      <el-col :span="22">
        <div style="display: flex;">
          <el-input
            style="width:300px;border: 0px;outline:none;"
            placeholder=""
            v-model="ccInput"
            @change="ccInputChange"
          >
          </el-input>
          <div style="width:100%;">
            <el-tag
              style="margin-right:10px;border-radius: 20px;"
              v-for="(tag, key) in msg.cc"
              :key="key"
              size="small"
              closable
              @close="handleCcClose(tag)"
              >{{ tag }}</el-tag
            >
          </div>
          <span
            class="iconfont umail-add return-row-padding"
            style="cursor: pointer;color:#409EFF;font-size:25px;"
            @click="showContactDialog"
            data-to="cc"
          ></span>
        </div>
      </el-col>
    </el-row>
    <el-row
      :gutter="20"
      style="padding-top:10px;border-bottom: 1px solid #e5e5e5;padding-left:20px;padding-right:20px;"
    >
      <el-col :span="2">
        <div style="color:#84898a;">
          <span>密送：</span>
        </div>
      </el-col>
      <el-col :span="22">
        <div style="display: flex;">
          <el-input
            style="width:300px;border: 0px;outline:none;"
            placeholder=""
            v-model="bccInput"
            @change="bccInputChange"
          >
          </el-input>
          <div style="width:100%;">
            <el-tag
              style="margin-right:10px;border-radius: 20px;"
              v-for="(tag, key) in msg.bcc"
              :key="key"
              size="small"
              closable
              @close="handleBccClose(tag)"
              >{{ tag }}</el-tag
            >
          </div>
          <span
            class="iconfont umail-add return-row-padding"
            style="cursor: pointer;color:#409EFF;font-size:25px;"
            @click="showContactDialog"
            data-to="bcc"
          ></span>
        </div>
      </el-col>
    </el-row>
    <el-row
      :gutter="20"
      style="padding-top:10px;margin-bottom:10px; border-bottom: 1px solid #e5e5e5;padding-left:20px;padding-right:20px;"
    >
      <el-col :span="2">
        <div style="color:#84898a;">
          <span>主题：</span>
        </div>
      </el-col>
      <el-col :span="22">
        <div style="display: flex;">
          <el-input
            style="width:100%;border: 0px;outline:none;"
            placeholder=""
            v-model="msg.subject"
            @change="subjectInputChange"
          >
          </el-input>
        </div>
      </el-col>
    </el-row>
    <!--附件列表-->
    <el-row :gutter="20" style="margin-bottom:10px;">
      <el-col :span="4" v-for="(file, index) in msg.attachment">
        <div
          style="color:#84898a;padding:5px;margin-bottom:10px;background: #f8f8f8;padding-left:20px;padding-right:20px;"
        >
          <el-row :gutter="20" type="flex" align="middle">
            <el-col
              :span="12"
              style="text-align: left;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;"
              ><i class="el-icon-document"></i>{{ file.name }}</el-col
            >
            <el-col
              :span="12"
              style="color:#409eff;cursor: pointer;text-align: right;"
              ><span @click="removeAttach" :data-index="index"
                >删除</span
              ></el-col
            >
          </el-row>
        </div>
      </el-col>
    </el-row>
    <!--添加联系人对话框设置-->
    <el-dialog
      title="联系人"
      :visible.sync="contactDialogFormVisible"
      width="400px"
    >
      <!--联系人列表-->
      <el-select v-model="contactEmail" placeholder="请选择联系人" size="small">
        <el-option
          v-for="contact in contacts"
          :key="contact.name"
          :label="contact.name"
          :value="contact.name + '<' + contact.email + '>'"
        >
        </el-option>
      </el-select>
      <div slot="footer" class="dialog-footer">
        <el-button
          type="primary"
          size="small"
          style="width:100px;"
          :disabled="contactEmail.trim() == ''"
          @click="doAddContact"
          >添加</el-button
        >
        <el-button size="small" style="width:100px;" @click="hideContactDialog"
          >取消</el-button
        >
      </div>
    </el-dialog>
    <!--正文编辑器-->
    <!--html编辑器-->
    <div v-if="type === 'html'">
      <div v-if="way == 'send'">
        <quill-editor
          v-model="msg.content"
          style="height:260px;"
          ref="myQuillEditor"
          :options="editorOption"
          @blur="onEditorBlur($event)"
          @focus="onEditorFocus($event)"
          @change="onEditorChange($event)"
        >
        </quill-editor>
      </div>
      <div v-else>
        <quill-editor
          v-model="msg.content"
          style="height:160px;"
          ref="myQuillEditor"
          :options="editorOption"
          @blur="onEditorBlur($event)"
          @focus="onEditorFocus($event)"
          @change="onEditorChange($event)"
        >
        </quill-editor>
        <div style="margin-top:60px;min-width: 1080px;" v-html="content0"></div>
      </div>
    </div>
    <!--纯文本编辑器-->
    <div v-if="type === 'plain'">
      <el-input
        type="textarea"
        :rows="12"
        placeholder="请输入内容"
        v-model="msg.content"
      >
      </el-input>
    </div>
    <!--底部按钮-->
    <el-row
      :gutter="20"
      type="flex"
      align="middle"
      style="margin-top:60px;padding-left:20px;padding-right:20px;"
    >
      <el-col :span="4">
        <div>
          <!--发件人-->
          <el-select
            v-model="msg.from"
            placeholder="请选择发件人"
            size="small"
            :disabled="way == 'reply'"
            @change="fromSelectChange"
          >
            <el-option
              v-for="acc in accounts"
              :key="acc.account"
              :label="acc.account"
              :value="acc.account"
            >
            </el-option>
          </el-select>
        </div>
      </el-col>
      <el-col :span="8">
        <div>
          <el-checkbox-group v-model="checkedList" @change="checkListChange">
            <el-checkbox
              :label="sendWay.label"
              v-for="sendWay in sendWayList"
            ></el-checkbox>
          </el-checkbox-group>
        </div>
      </el-col>
      <el-col :span="8">
        <div>
          <!-- <el-checkbox-group>
						<el-checkbox label="电子签名" ></el-checkbox>
					</el-checkbox-group> -->
        </div>
      </el-col>
    </el-row>
    <!--定时发送时间设置-->
    <el-row
      :gutter="20"
      type="flex"
      align="middle"
      style="margin-top:10px;padding-left:20px;padding-right:20px;"
      v-if="checkedList.indexOf('定时发送') != -1"
    >
      <el-col :span="3">
        <div>
          <span style="color:#84898a">发送时间：</span>
        </div>
      </el-col>
      <el-col :span="5">
        <div>
          <el-date-picker
            v-model="scheduleDate.date"
            type="date"
            size="small"
            value-format="yyyy-MM-dd"
            placeholder="选择日期"
          >
          </el-date-picker>
        </div>
      </el-col>
      <el-col :span="5">
        <div>
          <el-time-select
            v-model="scheduleDate.time"
            :picker-options="{
              start: '08:30',
              step: '00:15',
              end: '18:30'
            }"
            size="small"
            placeholder="选择时间"
          >
          </el-time-select>
        </div>
      </el-col>
    </el-row>
    <!--加密密码对话框设置-->
    <el-dialog title="邮件加密" :visible.sync="dialogFormVisible" width="30%">
      <el-form :model="secret">
        <el-form-item label="输入密码:">
          <el-input
            v-model="secret.one"
            autocomplete="off"
            show-password
          ></el-input>
        </el-form-item>
        <el-form-item label="确认密码:">
          <el-input
            v-model="secret.two"
            autocomplete="off"
            show-password
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="hideSecretDialog">取 消</el-button>
        <el-button type="primary" @click="confirmSecret">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import quillConfig from "../../quill-config.js";
import loading from "./loading.vue";
export default {
  name: "send",
  components: { "ums-loading": loading },
  created() {
    var _this = this;
    this.way = this.$route.params.way; //回复、转发
    this.type = this.$route.params.type; //html、plain
    this.accounts = this.$route.params.accounts;
    this.msg = this.$route.params.msg;
    this.fileList = this.$route.params.fileList;
    this.content0 = this.msg.content[0].text;
    this.msg.content = this.type == "plain" ? this.msg.content[0].text : "";
    if (this.msg.from == "" && this.accounts.length != 0) {
      this.msg.from = this.accounts[0].account;
    }
    this.uploadUrl = localStorage.getItem("base_url") + "/upload";
    this.$axios({
      method: "get",
      url:
        localStorage.getItem("base_url") +
        "/accountSign?account=" +
        this.msg.from +
        "&way=" +
        this.way +
        "&type=" +
        this.type,
      headers: { "Content-Type": "application/json", token: localStorage.token }
    }).then(function(res) {
      console.log(res.data);
      var _sign = res.data.data;
      _this.msg.content = _sign.content + _this.msg.content;
    });
    // this.msg.content = this.$route.params.msg.content[0].text;
    console.log("content:" + _this.content0);
    this.loadContact();
  },
  mounted() {
    if (this.type == "html") {
      quillConfig.initButton();
    }
  },
  data() {
    return {
      type: "",
      editorOption: quillConfig,
      to: "",
      toInput: "",
      ccInput: "",
      bccInput: "",
      msg: {
        from: "",
        to: [],
        cc: [],
        bcc: [],
        subject: "",
        date: "",
        content: "",
        type: ""
      },
      content0: "",
      date: "",
      checkedList: [],
      sendWayList: [
        { label: "定时发送" },
        { label: "对邮件加密" },
        { label: "群发单显" }
      ],
      dynamicTags: [],
      fileList: [],
      scheduleDate: {
        date: "",
        time: ""
      },
      dialogFormVisible: false,
      secret: {
        one: "",
        two: ""
      },
      formLabelWidth: "120px",
      contactDialogFormVisible: false,
      contactTo: "to",
      contactEmail: "",
      contacts: [],
      sendLoadingVisibled: false
    };
  },
  methods: {
    loadContact() {
      var _this = this;
      this.$axios({
        method: "get",
        url: localStorage.getItem("base_url") + "/contacts",
        headers: {
          "Content-Type": "application/json",
          token: localStorage.token
        }
      }).then(function(res) {
        console.log(res.data);
        var _contacts = res.data.data;
        _this.contacts = _contacts;
      });
    },
    showContactDialog(event) {
      this.contactTo = event.target.dataset.to;
      this.contactDialogFormVisible = true;
    },
    hideContactDialog() {
      this.contactDialogFormVisible = false;
    },
    doAddContact() {
      var _this = this;
      console.error(this.contactTo);
      if (_this.contactTo == "to") {
        if (_this.msg.to.indexOf(_this.contactEmail) == -1) {
          _this.msg.to.push(_this.contactEmail);
        }
      } else if (_this.contactTo == "cc") {
        if (_this.msg.cc.indexOf(_this.contactEmail) == -1) {
          _this.msg.cc.push(_this.contactEmail);
        }
      } else {
        if (_this.msg.bcc.indexOf(_this.contactEmail) == -1) {
          _this.msg.bcc.push(_this.contactEmail);
        }
      }
      this.contactDialogFormVisible = false;
    },
    saveToDraft() {
      var _this = this;
      console.log("saveToDraft");
      var data = {};
      var _msg = JSON.parse(JSON.stringify(this.msg));
      _msg.content = _msg.content + this.content0;
      data.msg = _msg;
      var raw = JSON.stringify(data);
      console.log(raw);
      this.$axios({
        method: "post",
        url: localStorage.getItem("base_url") + "/draft",
        data: {
          raw: JSON.stringify(data)
        },
        headers: {
          "Content-Type": "application/json",
          token: localStorage.token
        }
      }).then(function(res) {
        console.log(res.data);
        if (res.data.code == "200") {
          _this.$router.go(-1);
        }
      });
    },
    sendMessage() {
      var _this = this;
      var data = {};
      var _msg = JSON.parse(JSON.stringify(this.msg));
      _msg.type = this.type;
      _msg.content = _msg.content + this.content0;
      data.msg = _msg;
      // 发送方式
      var way = 0;
      if (this.checkedList.indexOf("定时发送") != -1) {
        way += 4;
        data.date = this.scheduleDate.date + " " + this.scheduleDate.time;
      }
      if (this.checkedList.indexOf("对邮件加密") != -1) {
        way += 8;
        data.secret = this.secret.one;
      }
      if (this.checkedList.indexOf("群发单显") != -1) {
        way += 2;
      }
      data.way = way;
      var raw = JSON.stringify(data);
      console.log(raw);
      _this.sendLoadingVisibled = true;
      this.$axios({
        method: "post",
        url: localStorage.getItem("base_url") + "/send",
        data: {
          raw: JSON.stringify(data)
        },
        headers: {
          "Content-Type": "application/json",
          token: localStorage.token
        }
      }).then(function(res) {
        console.log(res.data);
        if (res.data.code == "200") {
          _this.sendLoadingVisibled = false;
          _this.$message({ type: "success", message: "发送成功!" });
          _this.$router.go(-1);
        } else {
          _this.sendLoadingVisibled = false;
          _this.$message({
            type: "error",
            message: "发送失败，请检查收件人地址是否正确!"
          });
        }
      });
    },
    subjectInputChange() {},
    handleToClose(tag) {
      this.msg.to.splice(this.msg.to.indexOf(tag), 1);
    },
    handleCcClose(tag) {
      this.msg.cc.splice(this.msg.cc.indexOf(tag), 1);
    },
    handleBccClose(tag) {
      this.msg.bcc.splice(this.msg.bcc.indexOf(tag), 1);
    },
    toInputChange() {
      if (!this.toInput || this.toInput.trim() == "") {
        return false;
      }
      if (this.msg.to.length > 0) {
        for (var i in this.msg.to) {
          if (this.msg.to[i] == this.toInput) {
            return false;
          }
        }
      }
      this.msg.to.push(this.toInput + "<" + this.toInput + ">");
      this.toInput = "";
    },
    ccInputChange() {
      if (!this.ccInput || this.ccInput.trim() == "") {
        return false;
      }
      if (this.msg.cc.length > 0) {
        for (var i in this.msg.cc) {
          if (this.msg.cc[i] == this.ccInput) {
            return false;
          }
        }
      }
      this.msg.cc.push(this.ccInput + "<" + this.ccInput + ">");
      this.ccInput = "";
    },
    bccInputChange() {
      if (!this.bccInput || this.bccInput.trim() == "") {
        return false;
      }
      if (this.msg.bcc.length > 0) {
        for (var i in this.msg.bcc) {
          if (this.msg.bcc[i] == this.bccInput) {
            return false;
          }
        }
      }
      this.msg.bcc.push(this.bccInput + "<" + this.bccInput + ">");
      this.bccInput = "";
    },
    handleClose(tag) {
      this.$data.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1);
    },
    onEditorBlur() {
      //失去焦点事件
    },
    onEditorFocus() {
      //获得焦点事件
    },
    onEditorChange() {
      //内容改变事件
      // console.log(this.msg.content)
    },
    handleChange(file, fileList) {
      console.log(fileList);
      this.fileList = fileList.slice(-10);
    },
    removeAttach(event) {
      var startIndex = parseInt(event.currentTarget.dataset.index);
      var attachs = new Array();
      for (var i in this.msg.attachment) {
        if (startIndex != i) {
          attachs.push(this.msg.attachment[i]);
        }
      }
      this.msg.attachment = attachs;
    },
    uploadSectionFile(params) {
      var _this = this;
      console.log(params);
      // 不允许超过50M
      if (params.file.size >= 52428800) {
        _this.$message({
          type: "warning",
          message: "附件超过50M时大部分服务器不支持。"
        });
        return;
      }
      console.log(_this.msg.attachment);
      var _requestSize = 0;
      _this.msg.attachment.forEach(function(item, index) {
        _requestSize = _requestSize + parseInt(item.len);
      });
      if (params.file.size + _requestSize >= 52428800) {
        _this.$message({
          type: "warning",
          message: "附件超过50M时大部分服务器不支持。"
        });
        return;
      }
      var formData = new FormData();
      formData.append("file", params.file);
      this.$axios
        .post(localStorage.getItem("base_url") + "/upload", formData, {
          headers: { "Content-Type": "multipart/form-data" }
        })
        .then(function(res) {
          if (res.data.code === "200") {
            console.log("返回结果");
            console.log(res.data);
            _this.msg.attachment.push(res.data.data[0]);
            _this.msg.subject = res.data.data[0].name;
            console.log(_this.msg);
            console.log("返回结果1");
          }
        })
        .catch(function(err) {
          console.error(err);
        });
    },
    checkListChange(data) {
      if (data.indexOf("对邮件加密") != -1) {
        this.dialogFormVisible = true;
      }
    },
    hideSecretDialog() {
      var arr = new Array();
      for (var i in this.checkedList) {
        if (this.checkedList[i] != "对邮件加密") {
          arr.push(this.checkedList[i]);
        }
      }
      this.dialogFormVisible = false;
      this.checkedList = arr;
    },
    confirmSecret() {
      if (this.secret.one.trim() == "" || this.secret.two.trim() == "") {
        this.$message("请输入密码");
      } else {
        if (this.secret.one == this.secret.two) {
          this.dialogFormVisible = false;
        } else {
          this.$message("确认密码与输入密码不一致");
        }
      }
    },
    fromSelectChange(value) {
      var _this = this;
      this.$axios({
        method: "get",
        url:
          localStorage.getItem("base_url") +
          "/accountSign?account=" +
          value +
          "&way=" +
          _this.way +
          "&type=" +
          _this.type,
        headers: {
          "Content-Type": "application/json",
          token: localStorage.token
        }
      }).then(function(res) {
        console.log(res.data);
        var _sign = res.data.data;
        _this.msg.content = _sign.content;
      });
    }
  }
};
</script>

<style scoped>
.menu-padding {
  padding-right: 6px;
}
</style>
