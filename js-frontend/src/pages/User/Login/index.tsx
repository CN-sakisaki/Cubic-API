import { Footer } from '@/components';
import { userLoginUsingPost } from '@/services/js-backend/userController';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { LoginForm, ProFormCheckbox, ProFormText } from '@ant-design/pro-components';
import { Helmet, history, Link, useModel } from '@umijs/max';
import { Alert, message, Tabs } from 'antd';
import { createStyles } from 'antd-style';
import React, { useState } from 'react';
import Settings from '../../../../config/defaultSettings';

const useStyles = createStyles(({ token }) => ({
  container: {
    display: 'flex',
    flexDirection: 'column',
    height: '100vh',
    overflow: 'auto',
    backgroundImage:
      "url('https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/V-_oS6r-i7wAAAAAAAAAAAAAFl94AQBr')",
    backgroundSize: '100% 100%',
  },
  formActions: {
    marginBottom: 24,
    display: 'flex',
    alignItems: 'center', // 确保垂直居中对齐
    justifyContent: 'flex-start', // 改为左对齐，使所有元素靠左
  },
  registerLink: {
    marginLeft: 8, // 减少左边距，使其更靠近复选框
  },
  loginFormTitle: {
    marginTop: 24, // 增加顶部外边距，使标题下移
  },
}));

const LoginMessage: React.FC<{ content: string }> = ({ content }) => (
  <Alert style={{ marginBottom: 24 }} message={content} type="error" showIcon />
);

const Login: React.FC = () => {
  const [type, setType] = useState<string>('account');
  const { setInitialState } = useModel('@@initialState');
  const { styles } = useStyles();

  const handleSubmit = async (values: API.UserLoginRequest) => {
    try {
      const msg = await userLoginUsingPost(values);
      if (msg.data) {
        const urlParams = new URL(window.location.href).searchParams;
        setInitialState({ loginUser: msg.data });
        setTimeout(() => {
          history.push(urlParams.get('redirect') || '/');
        }, 100);
      }
    } catch (error) {
      console.error(error);
      message.error('登录失败，请重试！');
    }
  };

  return (
    <div className={styles.container}>
      <Helmet>
        <title>
          {'登录'}- {Settings.title}
        </title>
      </Helmet>
      <div style={{ flex: '1', padding: '32px 0' }}>
        <LoginForm
          contentStyle={{ minWidth: 280, maxWidth: '75vw' }}
          logo={
            <img
              alt="logo"
              src="/icons/icon_mo_fang.png"
              style={{ width: '60px', height: '60px' }}
            />
          }
          title="Cubic API"
          subTitle={
            <div className={styles.loginFormTitle}>
              <span>Cubic API 是个人云接口平台</span>
            </div>
          }
          initialValues={{ autoLogin: true }}
          onFinish={async (values) => await handleSubmit(values as API.UserLoginRequest)}
        >
          <Tabs activeKey={type} onChange={setType} centered>
            <Tabs.TabPane key="account" tab="账号密码登录" />
          </Tabs>

          {type === 'account' && (
            <>
              <ProFormText
                name="userAccount"
                fieldProps={{ size: 'large', prefix: <UserOutlined /> }}
                placeholder="用户名: admin or user"
                rules={[{ required: true, message: '用户名是必填项！' }]}
              />
              <ProFormText.Password
                name="userPassword"
                fieldProps={{ size: 'large', prefix: <LockOutlined /> }}
                placeholder="密码: 12345678"
                rules={[{ required: true, message: '密码是必填项！' }]}
              />
            </>
          )}

          <div className={styles.formActions}>
            <ProFormCheckbox noStyle name="autoLogin">
              自动登录
            </ProFormCheckbox>
            <Link to="/user/register" className={styles.registerLink}>
              新用户注册 &gt;&gt;
            </Link>
            <div style={{ marginLeft: 'auto' }}>
              <a target="_blank" rel="noreferrer" href="/user/forgot-password">
                忘记密码 ?
              </a>
            </div>
          </div>
        </LoginForm>
      </div>
      <Footer />
    </div>
  );
};

export default Login;
