<template>
  <div class="ums-page">
    <div class="ums-header">
      <div>
        <span
          class="iconfont umail-zuojiantou-up return-row-padding"
          style="cursor: pointer;"
          @click="$router.go(-1)"
        ></span>
        <span class="menu-padding">联系人管理</span>
      </div>
    </div>
    <div class="ums-container">
      <div class="ums-aside" style="width:480px;">
        <el-button
          @click="newContact"
          style="width:100%;text-align: center;border: none;border-bottom: 1px solid #EBEEF5;color:#84898a;"
          v-if="contacts.length == 0"
        >
          <span class="iconfont umail-hao menu-padding"></span>新建联系人
        </el-button>
        <el-table :data="contacts" :show-header="false">
          <el-table-column label="姓名" prop="contact">
            <template slot-scope="scope">
              <el-tooltip
                effect="light"
                :content="scope.row.name"
                placement="top"
              >
                <span class="ums-ellipsis">{{ scope.row.name }}</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" width="50">
            <template slot-scope="scope">
              <el-button @click="newContact" type="text" size="small"
                >新建</el-button
              >
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" width="50">
            <template slot-scope="scope">
              <el-button
                @click="modifyContact(scope.row)"
                type="text"
                size="small"
                >编辑</el-button
              >
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" width="50">
            <template slot-scope="scope">
              <el-button
                @click="deleteContact(scope.row)"
                type="text"
                size="small"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="ums-main">
        <!--修改状态-->
        <el-form
          ref="form"
          :model="contact"
          v-if="status == 'update'"
          class="contact-form"
          label-width="120px"
        >
          <el-form-item label="姓名:">
            <el-input
              v-model="contact.name"
              :disabled="true"
              size="small"
              style="width:250px"
            ></el-input>
          </el-form-item>
          <el-form-item label="邮箱:">
            <el-input
              v-model="contact.email"
              size="small"
              style="width:250px"
            ></el-input>
          </el-form-item>
          <el-form-item label="移动电话:">
            <el-input
              v-model="contact.movephone"
              size="small"
              type="number"
              style="width:250px"
            ></el-input>
          </el-form-item>
          <el-form-item label="公司:">
            <el-input
              v-model="contact.company"
              size="small"
              style="width:250px"
            ></el-input>
          </el-form-item>
          <el-form-item label="工作电话:">
            <el-input
              v-model="contact.workphone"
              size="small"
              type="number"
              style="width:250px"
            ></el-input>
          </el-form-item>
          <el-form-item label="备注:">
            <el-input
              v-model="contact.remark"
              size="small"
              type="textarea"
              style="width:250px"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="small"
              style="width:100px;"
              @click="doModifyContact"
              >确定</el-button
            >
            <el-button
              size="small"
              style="width:100px;"
              @click="status = 'init'"
              >取消</el-button
            >
          </el-form-item>
        </el-form>
        <!--新建状态-->
        <el-form
          :model="newcontact"
          v-if="status == 'create'"
          class="contact-form"
          label-width="120px"
        >
          <el-form-item label="姓名:">
            <el-input
              v-model="newcontact.name"
              size="small"
              style="width:250px"
            ></el-input>
          </el-form-item>
          <el-form-item label="邮箱:">
            <el-input
              v-model="newcontact.email"
              size="small"
              style="width:250px"
            ></el-input>
          </el-form-item>
          <el-form-item label="移动电话:">
            <el-input
              v-model="newcontact.movephone"
              size="small"
              type="number"
              style="width:250px"
            ></el-input>
          </el-form-item>
          <el-form-item label="公司:">
            <el-input
              v-model="newcontact.company"
              size="small"
              style="width:250px"
            ></el-input>
          </el-form-item>
          <el-form-item label="工作电话:">
            <el-input
              v-model="newcontact.workphone"
              size="small"
              type="number"
              style="width:250px"
            ></el-input>
          </el-form-item>
          <el-form-item label="备注:">
            <el-input
              v-model="newcontact.remark"
              size="small"
              type="textarea"
              style="width:250px"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="small"
              style="width:100px;"
              :disabled="
                newcontact.name.trim() == '' ||
                  !/[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+/.test(
                    newcontact.email
                  )
              "
              @click="doNewContact"
              >创建</el-button
            >
            <el-button
              size="small"
              style="width:100px;"
              @click="status = 'init'"
              >取消</el-button
            >
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "contact",
  created() {
    this.loadContact();
  },
  mounted() {},
  computed: {},
  data() {
    return {
      status: "",
      contacts: [],
      contact: {},
      newcontact: {
        name: "",
        email: "",
        movephone: "",
        company: "",
        workphone: "",
        remark: ""
      }
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
    newContact() {
      this.status = "create";
      this.newcontact = JSON.parse(
        JSON.stringify({
          name: "",
          email: "",
          movephone: "",
          company: "",
          workphone: "",
          remark: ""
        })
      );
    },
    doNewContact() {
      var _this = this;
      var exist = false;
      // 去除重复联系人
      _this.contacts.forEach(function(item, index) {
        if (_this.newcontact.name == item.name) {
          exist = true;
        }
      });
      if (exist) {
        _this.$message({ type: "error", message: "该联系人已存在" });
      } else {
        this.$axios
          .post(
            localStorage.getItem("base_url") + "/contacts",
            _this.newcontact,
            {
              headers: {
                "Content-Type": "application/json",
                token: localStorage.token,
                type: "save"
              }
            }
          )
          .then(function(res) {
            console.log(res.data);
            if (res.data.code == "200") {
              _this.$message({
                type: "success",
                message: "添加联系人成功!",
                duration: 1000
              });
              _this.loadContact();
              _this.status = "init";
            }
          });
      }
    },
    modifyContact(row) {
      console.log(row);
      this.status = "update";
      this.contact = JSON.parse(JSON.stringify(row));
    },
    doModifyContact() {
      var _this = this;
      this.$confirm("您确认要修改" + _this.contact.name + "吗？", "确认", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          _this.$axios
            .post(
              localStorage.getItem("base_url") + "/contacts",
              _this.contact,
              {
                headers: {
                  "Content-Type": "application/json",
                  token: localStorage.token,
                  type: "update"
                }
              }
            )
            .then(function(res) {
              console.log(res.data);
              if (res.data.code == "200") {
                _this.$message({
                  type: "success",
                  message: "修改联系人成功!",
                  duration: 1000
                });
                _this.loadContact();
                _this.status = "init";
              }
            });
        })
        .catch(() => {});
    },
    deleteContact(row) {
      var _this = this;
      this.$confirm("您确认要永久删除" + row.name + "吗？", "确认", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          _this.$axios
            .post(localStorage.getItem("base_url") + "/contacts", row, {
              headers: {
                "Content-Type": "application/json",
                token: localStorage.token,
                type: "delete"
              }
            })
            .then(function(res) {
              console.log(res.data);
              if (res.data.code == "200") {
                _this.$message({
                  type: "success",
                  message: "删除联系人成功!",
                  duration: 1000
                });
                _this.loadContact();
                _this.status = "init";
              }
            });
        })
        .catch(() => {});
    },
    addContact() {
      var _this = this;
      this.$axios
        .post(localStorage.getItem("base_url") + "/contacts", this.contact, {
          headers: {
            "Content-Type": "application/json",
            token: localStorage.token,
            type: "save"
          }
        })
        .then(function(res) {
          console.log(res.data);
          if (res.data.code == "200") {
            _this.$message({
              type: "success",
              message: "创建成功!"
            });
          }
        });
    }
  }
};
</script>

<style scoped>
.contact-form {
  margin: 10px auto;
  width: 400px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 10px;
}
</style>
