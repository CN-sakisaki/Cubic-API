import { Footer } from '@/components';
import {
  getCaptchaUsingPost,
  userEmailLoginUsingPost,
  userLoginUsingPost,
} from '@/services/js-backend/userController';
import { LockOutlined, MailOutlined, UserOutlined } from '@ant-design/icons';
import { LoginForm, ProFormCheckbox, ProFormText } from '@ant-design/pro-components';
import { ProFormCaptcha } from '@ant-design/pro-form';
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

  const doLogin = (res: any) => {
    if (res.data && res.code === 0) {
      message.success('登陆成功');
      setTimeout(() => {
        const urlParams = new URL(window.location.href).searchParams;
        history.push(urlParams.get('redirect') || '/');
      }, 100);
      setInitialState({ loginUser: res.data, settings: Settings });
    }
  };

  const handleSubmit = async (values: API.UserLoginRequest | API.UserEmailLoginRequest) => {
    try {
      let res;
      if (type === 'account') {
        res = await userLoginUsingPost(values as API.UserLoginRequest);
      } else {
        res = await userEmailLoginUsingPost(values as API.UserEmailLoginRequest);
      }

      doLogin(res);
    } catch (error) {
      console.error(error);
      message.error('登录失败，请重试！');
    }
  };

  const handleGetCaptcha = async (emailAccount: string) => {
    try {
      const res = await getCaptchaUsingPost({ emailAccount });
      if (res.data && res.code === 0) {
        message.success('获取验证码成功');
      }
    } catch (error) {
      console.error(error);
      message.error('验证码发送失败，请重试！');
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
          onFinish={async (values) => {
            if (type === 'account') {
              await handleSubmit(values as API.UserLoginRequest);
            } else {
              await handleSubmit(values as API.UserLoginRequest);
            }
          }}
        >
          <Tabs activeKey={type} onChange={setType} centered>
            <Tabs.TabPane key="account" tab="账号密码登录" />
            <Tabs.TabPane key="email" tab="邮箱登录" />
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

          {type === 'email' && (
            <>
              <ProFormText
                fieldProps={{
                  size: 'large',
                  prefix: <MailOutlined />,
                }}
                name="emailAccount"
                placeholder={'请输入邮箱账号！'}
                rules={[
                  {
                    required: true,
                    message: '邮箱账号是必填项！',
                  },
                  {
                    pattern: /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/,
                    message: '不合法的邮箱账号！',
                  },
                ]}
              />
              <ProFormCaptcha
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined />,
                }}
                captchaProps={{
                  size: 'large',
                }}
                placeholder={'请输入验证码！'}
                captchaTextRender={(timing, count) => {
                  if (timing) {
                    return `${count} ${'秒后重新获取'}`;
                  }
                  return '获取验证码';
                }}
                phoneName={'emailAccount'}
                name="captcha"
                rules={[
                  {
                    required: true,
                    message: '验证码是必填项！',
                  },
                ]}
                onGetCaptcha={handleGetCaptcha}
              />
            </>
          )}
          <ProFormCheckbox
            initialValue={true}
            name="agreeToAnAgreement"
            rules={[
              () => ({
                validator(_, value) {
                  if (!value) {
                    return Promise.reject(new Error('同意协议后才可以登录'));
                  }
                  return Promise.resolve();
                },
                required: true,
              }),
            ]}
          >
            同意并接受《
            <a target={'_blank'} href={''} rel="noreferrer">
              隐私协议
            </a>
            》《
            <a target={'_blank'} href={''} rel="noreferrer">
              用户协议
            </a>
            》
          </ProFormCheckbox>

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
